(function () {
    const API = "";
    let accessToken = null;

    function out(data) {
        const element = document.querySelector("#out");

        if (!element) {
            return;
        }

        element.textContent = (typeof data === "string") ? data : JSON.stringify(data, null, 2);
    }

    async function api(path, {method = "GET", body, retry = true} = {}) {
        const headers = {"Content-Type": "application/json"};

        if (accessToken) {
            headers["Authorization"] = `Bearer ${accessToken}`;
        }

        const res = await fetch(API + path, {
            method,
            headers,
            credentials: "include",
            body: body ? JSON.stringify(body) : undefined,
        });

        if (res.status === 401 && retry) {
            const r = await fetch(API + "/auth/refresh", {method: "POST", credentials: "include"});

            if (r.ok) {
                const j = await r.json();
                accessToken = j.accessToken || null;
                return api(path, {method, body, retry: false});
            }
        }

        const text = await res.text();
        const result = {status: res.status};

        Object.defineProperty(result, "headers", {
            value: {get: (name) => res.headers.get(name)},
            enumerable: false
        });

        try {
            result.json = JSON.parse(text);
        } catch {
            result.text = text;
        }

        return result;
    }

    async function requireAuth({redirectToLogin = false} = {}) {
        if (!accessToken) {
            const r = await fetch("/auth/refresh", {method: "POST", credentials: "include"});

            if (r.ok) {
                const j = await r.json();
                accessToken = j.accessToken || null;
            }
        }
        if (!accessToken) {
            if (redirectToLogin) {
                const returnTo = encodeURIComponent(location.pathname + location.search);
                location.href = `/login.html?returnTo=${returnTo}`;
            }

            return false;
        }

        return true;
    }

    async function logoutAndGoHome() {
        try {
            await fetch("/auth/logout", {method: "POST", credentials: "include"});
        } finally {
            accessToken = null;
            location.href = "/index.html";
        }
    }

    async function bootstrapAfterGoogle() {
        const params = new URLSearchParams(location.search);
        if (params.get("loggedIn") === "google") {
            const r = await fetch("/auth/refresh", {method: "POST", credentials: "include"});

            if (r.ok) {
                const j = await r.json();
                accessToken = j.accessToken || null;
                out({
                    status: 200, json: {
                        message: "Google 로그인 완료"
                    }
                });
            } else {
                out({status: r.status, text: "refresh 실패"});
            }
        }
    }

    function escapeHtml(s) {
        return s == null ? "" : s.replace(/[&<>"']/g, (c) => ({
            "&": "&amp;", "<": "&lt;", ">": "&gt;", "\"": "&quot;", "'": "&#39;"
        })[c]);
    }

    function qs(key, search = location.search) {
        return new URLSearchParams(search).get(key);
    }

    async function ensureAccessToken() {
        if (!accessToken) {
            const r = await fetch("/auth/refresh", {method: "POST", credentials: "include"});
            if (r.ok) {
                const j = await r.json();
                accessToken = j.accessToken || null;
            }
        }
    }

    // index.html
    async function initIndex() {
        const btn = document.getElementById("logout");

        if (btn) {
            btn.onclick = logoutAndGoHome;
        }

        await bootstrapAfterGoogle();
    }

    // signup.html
    function initSignup() {
        const btn = document.querySelector("#signup");

        if (!btn) {
            return;
        }

        btn.onclick = async () => {
            const name = document.querySelector("#name").value.trim();
            const email = document.querySelector("#email").value.trim();
            const password = document.querySelector("#pw").value;

            const r = await api("/auth/signup", {method: "POST", body: {name, email, password}});
            out(r);

            if (r.status === 201) {
                location.href = "/login.html";
            }
        };
    }

    // login.html
    function initLogin() {
        const btn = document.querySelector("#login");

        if (!btn) {
            return;
        }

        btn.onclick = async () => {
            const email = document.querySelector("#email").value.trim();
            const password = document.querySelector("#pw").value;

            const r = await api("/auth/login", {method: "POST", body: {email, password}});
            out(r);

            if (r.status === 200) {
                if (r.json && r.json.accessToken) {
                    accessToken = r.json.accessToken;
                    location.href = "/index.html";
                    return;
                }

                const ex = await fetch("/auth/refresh", {method: "POST", credentials: "include"});

                if (ex.ok) {
                    const j = await ex.json();
                    accessToken = j.accessToken || null;
                    location.href = "/index.html";
                } else {
                    out({status: ex.status, text: "로그인 후 토큰 교환 실패"});
                }
            }
        };
    }

    // myPage.html
    function initMyPage() {
        const btn = document.querySelector("#load");
        if (btn) {
            btn.onclick = async () => {
                const r = await api("/auth/myInfo");
                out(r);
            };
        }

        bootstrapAfterGoogle();
    }

    // posts.html
    async function initPosts() {
        const logout = document.getElementById("logout");

        if (logout) {
            logout.onclick = logoutAndGoHome;
        }

        await ensureAccessToken();

        const listElement = document.getElementById("list");

        if (!listElement) {
            return;
        }

        const res = await api("/posts", {method: "GET"});

        if (res.status !== 200 || !Array.isArray(res.json)) {
            listElement.textContent = "목록을 불러오지 못했습니다.";
            return;
        }

        if (res.json.length === 0) {
            listElement.textContent = "게시글이 없습니다.";
            return;
        }

        const frag = document.createDocumentFragment();

        for (const p of res.json) {
            const div = document.createElement("div");
            div.style.margin = "8px 0";
            const id = p.postId ?? p.id;

            div.innerHTML = `
                <a href="/post.html?id=${encodeURIComponent(id)}"><b>${escapeHtml(p.title)}</b></a>
                <div style="font-size:12px;color:#666">작성자: ${escapeHtml(p.authorName ?? "")}</div>
            `;

            frag.appendChild(div);
        }

        listElement.innerHTML = "";
        listElement.appendChild(frag);
    }

    // post.html
    async function initPost() {
        const logout = document.getElementById("logout");

        if (logout) {
            logout.onclick = logoutAndGoHome;
        }

        const postId = Number(qs("id"));

        if (!postId) {
            alert("잘못된 접근입니다.");
            location.href = "/posts.html";
            return;
        }

        let currentUser = {id: null, role: null};
        let currentPost = null;

        const isAdmin = () => currentUser.role === "ROLE_ADMIN";
        const isOwner = () =>
            currentUser.id && currentPost && String(currentUser.id) === String(currentPost.authorId);

        (function ensurePostActionsContainer() {
            if (!document.getElementById("postActions")) {
                const host = document.querySelector("#post");
                const actions = document.createElement("div");
                actions.id = "postActions";
                actions.style.display = "none";
                actions.style.gap = "8px";
                actions.style.marginTop = "10px";

                const btnEdit = document.createElement("button");
                btnEdit.id = "btnEdit";
                btnEdit.textContent = "수정";

                const btnDelete = document.createElement("button");
                btnDelete.id = "btnDelete";
                btnDelete.textContent = "삭제";

                actions.append(btnEdit, btnDelete);

                if (host) {
                    host.appendChild(actions);
                }
            }
        })();

        async function loadMe() {
            await ensureAccessToken();

            const guard = document.getElementById("commentGuard");
            const ta = document.getElementById("commentContent");
            const btn = document.getElementById("btnCommentCreate");

            if (!accessToken) {
                if (guard) {
                    guard.style.display = "";
                }

                if (ta) {
                    ta.style.display = "none";
                }

                if (btn) {
                    btn.style.display = "none";
                }

                return;
            }
            const me = await api("/auth/myInfo");

            if (me.status === 200 && me.json) {
                currentUser.id = me.json.id;
                currentUser.role = me.json.role;

                if (guard) {
                    guard.style.display = "none";
                }

                if (ta) {
                    ta.style.display = "";
                }

                if (btn) {
                    btn.style.display = "";
                }
            }
        }

        async function loadPost() {
            const r = await api(`/posts/${postId}`);
            const bodyEl = document.getElementById("postBody");

            if (r.status !== 200 || !r.json) {
                if (bodyEl) {
                    bodyEl.textContent = "게시글을 불러오지 못했습니다.";
                }

                return;
            }

            currentPost = r.json;
            const title = document.getElementById("title");
            const meta = document.getElementById("postMeta");

            if (title) {
                title.textContent = currentPost.title;
            }

            if (meta) {
                meta.textContent = `작성자: ${currentPost.authorName ?? ""}`;
            }

            if (bodyEl) {
                bodyEl.textContent = currentPost.content ?? "";
            }

            renderPostActions();
        }

        function renderPostActions() {
            const actions = document.getElementById("postActions");

            if (!actions) {
                return;
            }

            actions.style.display = (isAdmin() || isOwner()) ? "flex" : "none";
        }

        async function loadComments() {
            const r = await api(`/posts/${postId}/comments`);
            const listEl = document.getElementById("commentList");

            if (!listEl) {
                return;
            }

            if (r.status !== 200 || !Array.isArray(r.json)) {
                listEl.textContent = "댓글을 불러오지 못했습니다.";
                return;
            }
            if (r.json.length === 0) {
                listEl.innerHTML = '<div class="muted">첫 댓글을 남겨보세요!</div>';
                return;
            }

            const frag = document.createDocumentFragment();
            r.json.forEach((c) => {
                const wrap = document.createElement("div");
                wrap.className = "comment";
                wrap.id = `comment-${c.commentId}`;

                const body = document.createElement("div");
                body.className = "content";
                body.textContent = c.content ?? "";

                const meta = document.createElement("div");
                meta.className = "meta";
                meta.textContent = `작성자: ${escapeHtml(c.authorName ?? "")}`;

                wrap.appendChild(meta);
                wrap.appendChild(body);

                const canEdit = currentUser.id && (isAdmin() || String(currentUser.id) === String(c.authorId));
                if (canEdit) {
                    const actions = document.createElement("div");
                    actions.className = "comment-actions";

                    const btnEdit = document.createElement("button");
                    btnEdit.textContent = "수정";
                    btnEdit.onclick = () => enterEditMode(wrap, c);

                    const btnDel = document.createElement("button");
                    btnDel.textContent = "삭제";
                    btnDel.onclick = () => deleteComment(c.commentId);

                    actions.append(btnEdit, btnDel);
                    wrap.appendChild(actions);
                }
                frag.appendChild(wrap);
            });

            listEl.innerHTML = "";
            listEl.appendChild(frag);
        }

        function enterEditMode(container, c) {
            const original = container.querySelector(".content");
            const actions = container.querySelector(".comment-actions");

            const ta = document.createElement("textarea");
            ta.value = c.content || "";
            ta.rows = 4;
            ta.style.width = "100%";

            const btnSave = document.createElement("button");
            btnSave.textContent = "저장";
            btnSave.onclick = async () => {
                const content = ta.value.trim();

                if (!content) {
                    return;
                }

                const r = await api(`/comments/${c.commentId}`, {method: "PATCH", body: {content}});

                if (r.status === 200) {
                    await loadComments();
                }
            };

            const btnCancel = document.createElement("button");
            btnCancel.textContent = "취소";
            btnCancel.onclick = () => loadComments();

            if (original) {
                original.replaceWith(ta);
            }

            if (actions) {
                actions.replaceChildren(btnSave, btnCancel);
            }
        }

        async function deleteComment(commentId) {
            if (!confirm("댓글을 삭제하시겠습니까 ?")) {
                return;
            }

            const r = await api(`/comments/${commentId}`, {method: "DELETE"});

            if (r.status === 204) {
                await loadComments();
            }
        }

        const createBtn = document.getElementById("btnCommentCreate");
        if (createBtn) {
            createBtn.onclick = async () => {
                const ok = await requireAuth({redirectToLogin: true});

                if (!ok) {
                    return;
                }

                const content = (document.getElementById("commentContent").value || "").trim();

                if (!content) {
                    return;
                }

                const r = await api(`/posts/${postId}/comments`, {method: "POST", body: {content}});

                if (r.status === 201) {
                    document.getElementById("commentContent").value = "";
                    await loadComments();
                }
            };
        }

        const btnEditPost = document.getElementById("btnEdit");
        if (btnEditPost) {
            btnEditPost.onclick = () => {
                if (!currentPost) {
                    return;
                }

                if (document.getElementById("editTitle")) {
                    return;
                }

                const titleEl = document.getElementById("title");
                const bodyEl = document.getElementById("postBody");
                const actions = document.getElementById("postActions");

                const editTitle = document.createElement("input");
                editTitle.id = "editTitle";
                editTitle.type = "text";
                editTitle.style.width = "100%";
                editTitle.value = currentPost.title || "";

                if (titleEl) {
                    titleEl.replaceWith(editTitle);
                }

                const editBody = document.createElement("textarea");
                editBody.id = "editBody";
                editBody.rows = 10;
                editBody.style.width = "100%";
                editBody.value = currentPost.content || "";

                if (bodyEl) {
                    bodyEl.replaceWith(editBody);
                }

                if (actions) {
                    actions.replaceChildren();

                    const btnSave = document.createElement("button");
                    btnSave.textContent = "저장";
                    btnSave.onclick = async () => {
                        const title = editTitle.value.trim();
                        const content = editBody.value.trim();

                        if (!title || !content) {
                            return;
                        }

                        const r = await api(`/posts/${postId}`, {
                            method: "PATCH",
                            body: {title, content}
                        });

                        if (r.status === 200) {
                            location.href = `/post.html?id=${encodeURIComponent(postId)}`;
                        } else {
                            alert("수정에 실패했습니다.");
                        }
                    };

                    const btnCancel = document.createElement("button");
                    btnCancel.textContent = "취소";
                    btnCancel.onclick = async () => {
                        location.href = `/post.html?id=${encodeURIComponent(postId)}`;
                    };

                    actions.append(btnSave, btnCancel);
                }
            };
        }

        const btnDeletePost = document.getElementById("btnDelete");

        if (btnDeletePost) {
            btnDeletePost.onclick = async () => {
                if (!confirm("이 글을 삭제하시겠습니까?")) {
                    return;
                }

                const r = await api(`/posts/${postId}`, {method: "DELETE"});

                if (r.status === 204) {
                    location.href = "/posts.html";
                } else {
                    alert("삭제에 실패했습니다.");
                }
            };
        }

        await loadMe();
        await loadPost();
        await loadComments();
    }

    async function initCreatePost() {
        const ok = await requireAuth({redirectToLogin: true});

        if (!ok) {
            return;
        }

        const cancel = document.getElementById("cancel");

        if (cancel) {
            cancel.onclick = () => (location.href = "/posts.html");
        }

        const save = document.getElementById("save");

        if (!save) {
            return;
        }

        save.onclick = async () => {
            const title = document.getElementById("title").value.trim();
            const content = document.getElementById("content").value.trim();

            if (!title || !content) {
                out({status: 400, text: "제목/내용을 입력하세요."});
                return;
            }

            const r = await api("/posts", {method: "POST", body: {title, content}});
            out(r);
            const status = (r && typeof r.status === "number") ? r.status : 0;

            if (status >= 200 && status < 300) {
                location.href = "/posts.html";
            } else {
                alert("작성에 실패했습니다.");
            }
        };
    }

    // admin.html
    async function initAdmin() {
        const ok = await requireAuth({redirectToLogin: true});

        if (!ok) {
            return;
        }

        const who = await api("/auth/myInfo");
        out(who);

        const s = document.getElementById("status");
        if (s) {
            if (who.status === 200 && who.json && who.json.role) {
                s.textContent = (who.json.role === "ROLE_ADMIN") ? "관리자입니다." : "관리자가 아닙니다.";
            } else if (who.status === 401) {
                s.textContent = "로그인이 필요합니다.";
            } else if (who.status === 403) {
                s.textContent = "관리자가 아닙니다.";
            } else {
                s.textContent = "인증 실패 또는 정보 확인 불가";
            }
        }

        const ping = document.getElementById("btn-ping");

        if (ping) {
            ping.onclick = async () => out(await api("/admin/checkAdmin"));
        }

        const logout = document.getElementById("logout");

        if (logout) {
            logout.onclick = logoutAndGoHome;
        }
    }

    const routes = {
        "/": initIndex,
        "/index.html": initIndex,
        "/signup.html": initSignup,
        "/login.html": initLogin,
        "/myPage.html": initMyPage,
        "/posts.html": initPosts,
        "/post.html": initPost,
        "/createPost.html": initCreatePost,
        "/admin.html": initAdmin,
    };

    window.AppAPI = {
        api,
        out,
        requireAuth,
        logoutAndGoHome,
        bootstrapAfterGoogle,
        setAccessToken: (t) => {
            accessToken = t;
        },
        getAccessToken: () => accessToken,
    };

    document.addEventListener("DOMContentLoaded", () => {
        const path = location.pathname || "/";
        const init = routes[path] || (() => {
        });
        try {
            init();
        } catch (e) {
            console.error("init error:", e);
        }
    });
})();
