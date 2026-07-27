<script lang="ts">
    import { goto } from "$app/navigation";
    import { onMount } from "svelte";
 
    const API_BASE_URL = "http://localhost:8080";
    const USER_ID = 1;
    const DEFAULT_ICON_URL = "/images/ProfileIcon.png";
 
    const Role = {
        USER: "USER",
        ORGANIZER: "ORGANIZER",
        DANCER: "DANCER",
        ADMIN: "ADMIN",
    } as const;
 
    type Role = (typeof Role)[keyof typeof Role];
 
    const Genre = {
        HIPHOP: "HIPHOP",
        JAZZ: "JAZZ",
        BREAK: "BREAK",
        LOCK: "LOCK",
        POP: "POP",
        HOUSE: "HOUSE",
        KPOP: "KPOP",
        FREESTYLE: "FREESTYLE",
        OTHER: "OTHER",
    } as const;
 
    type Genre = (typeof Genre)[keyof typeof Genre];
 
    const roleOptions = [
        { label: "一般ユーザー", value: Role.USER },
        { label: "主催者", value: Role.ORGANIZER },
        { label: "ダンサー", value: Role.DANCER },
        { label: "管理者", value: Role.ADMIN },
    ];
 
    const genreOptions = [
        { label: "HIPHOP", value: Genre.HIPHOP },
        { label: "JAZZ", value: Genre.JAZZ },
        { label: "BREAK", value: Genre.BREAK },
        { label: "LOCK", value: Genre.LOCK },
        { label: "POP", value: Genre.POP },
        { label: "HOUSE", value: Genre.HOUSE },
        { label: "KPOP", value: Genre.KPOP },
        { label: "FREESTYLE", value: Genre.FREESTYLE },
        { label: "OTHER", value: Genre.OTHER },
    ];
 
    type ProfileEditForm = {
        username: string;
        role: Role;
        email: string;
        newPassword: string;
        profileText: string;
        iconFile: File | null;
        iconPreviewUrl: string;
        genres: Genre[];
        birthDate: string;
    };
 
    type UserProfileResponse = {
        id?: number;
        name?: string;
        username?: string;
        email?: string;
        role?: string;
        profileText?: string | null;
        profile_text?: string | null;
        profileImageUrl?: string | null;
        profile_image_url?: string | null;
        genre?: string | null;
        genres?: string[] | string | null;
        birthDate?: string | null;
        birth_date?: string | null;
    };
 
    let form = $state<ProfileEditForm>({
        username: "",
        role: Role.USER,
        email: "",
        newPassword: "",
        profileText: "",
        iconFile: null,
        iconPreviewUrl: DEFAULT_ICON_URL,
        genres: [],
        birthDate: "",
    });
 
    let errorMessage = $state("");
    let successMessage = $state("");
    let isLoading = $state(false);
    let isSaving = $state(false);
 
    let shouldShowDefaultIcon = $state(false);
    let currentObjectUrl: string | null = null;
 
    const displayIconPreviewUrl = $derived(
        shouldShowDefaultIcon
            ? DEFAULT_ICON_URL
            : form.iconPreviewUrl || DEFAULT_ICON_URL,
    );
 
    function normalizeGenres(value: string[] | string | null | undefined): Genre[] {
        if (!value) {
            return [];
        }
 
        const rawGenres = Array.isArray(value)
            ? value
            : value.split(",");
 
        return rawGenres
            .map((genre) => genre.trim())
            .filter((genre): genre is Genre => isGenre(genre));
    }
 
    function toggleGenre(genre: Genre) {
        if (form.genres.includes(genre)) {
            form.genres = form.genres.filter((selectedGenre) => selectedGenre !== genre);
            return;
        }
 
        form.genres = [...form.genres, genre];
    }
 
    function isRole(value: unknown): value is Role {
        return Object.values(Role).includes(value as Role);
    }
 
    function isGenre(value: unknown): value is Genre {
        return Object.values(Genre).includes(value as Genre);
    }
 
    function toDateInputValue(value: string | null | undefined): string {
        if (!value) {
            return "";
        }
 
        return value.slice(0, 10);
    }
 
    function normalizeImageUrl(value: string | null | undefined): string {
        if (!value || !value.trim()) {
            return DEFAULT_ICON_URL;
        }
 
        const imageUrl = value.trim();
 
        if (
            imageUrl === "null" ||
            imageUrl === "undefined" ||
            imageUrl === "None"
        ) {
            return DEFAULT_ICON_URL;
        }
 
        if (
            imageUrl.startsWith("http://") ||
            imageUrl.startsWith("https://") ||
            imageUrl.startsWith("data:") ||
            imageUrl.startsWith("blob:")
        ) {
            return imageUrl;
        }
 
        if (imageUrl.startsWith("/uploads/")) {
            return `${API_BASE_URL}${imageUrl}`;
        }
 
        if (imageUrl.startsWith("uploads/")) {
            return `${API_BASE_URL}/${imageUrl}`;
        }
 
        if (imageUrl.startsWith("/")) {
            return imageUrl;
        }
 
        return `/${imageUrl}`;
    }
 
    function handleIconPreviewError(event: Event) {
        const img = event.currentTarget as HTMLImageElement;
 
        if (img.src.endsWith(DEFAULT_ICON_URL)) {
            return;
        }
 
        shouldShowDefaultIcon = true;
        form.iconPreviewUrl = DEFAULT_ICON_URL;
    }
 
    async function loadProfile() {
        isLoading = true;
        errorMessage = "";
        successMessage = "";
 
        try {
            const response = await fetch(`${API_BASE_URL}/api/users/${USER_ID}/profile`);
 
            if (!response.ok) {
                throw new Error(`プロフィール取得に失敗しました。status=${response.status}`);
            }
 
            const data: UserProfileResponse = await response.json();
 
            const role = isRole(data.role) ? data.role : Role.USER;
            const genres = normalizeGenres(data.genres ?? data.genre);
 
            form.username = data.name ?? data.username ?? "";
            form.email = data.email ?? "";
            form.role = role;
            form.profileText = data.profileText ?? data.profile_text ?? "";
            form.iconPreviewUrl = normalizeImageUrl(
                data.profileImageUrl ?? data.profile_image_url,
            );
            shouldShowDefaultIcon = false;
            form.genres = genres;
            form.birthDate = toDateInputValue(data.birthDate ?? data.birth_date);
            form.newPassword = "";
            form.iconFile = null;
        } catch (error) {
            console.error(error);
            errorMessage = "プロフィール情報を取得できませんでした。APIの起動状態を確認してください。";
            form.iconPreviewUrl = DEFAULT_ICON_URL;
            shouldShowDefaultIcon = true;
        } finally {
            isLoading = false;
        }
    }
 
    function handleIconChange(event: Event) {
        const input = event.target as HTMLInputElement;
        const file = input.files?.[0];
 
        if (!file) {
            return;
        }
 
        if (!file.type.startsWith("image/")) {
            errorMessage = "画像ファイルを選択してください。";
            return;
        }
 
        if (currentObjectUrl) {
            URL.revokeObjectURL(currentObjectUrl);
        }
 
        currentObjectUrl = URL.createObjectURL(file);
 
        form.iconFile = file;
        form.iconPreviewUrl = currentObjectUrl;
        shouldShowDefaultIcon = false;
        errorMessage = "";
        successMessage = "";
    }
 
    function validateForm(): boolean {
        if (!form.username.trim()) {
            errorMessage = "ユーザーネームを入力してください。";
            return false;
        }
 
        if (!form.email.trim()) {
            errorMessage = "メールアドレスを入力してください。";
            return false;
        }
 
        if (!form.role) {
            errorMessage = "ロールを選択してください。";
            return false;
        }
 
        errorMessage = "";
        return true;
    }
 
    async function handleSubmit() {
        console.log("保存ボタンが押されました");
 
        if (!validateForm()) {
            return;
        }
 
        isSaving = true;
        errorMessage = "";
        successMessage = "";
 
        const profilePayload = {
            name: form.username,
            role: form.role,
            email: form.email,
            newPassword: form.newPassword || null,
            profileText: form.profileText || null,
            profileImageUrl: form.iconPreviewUrl || null,
            genres: form.genres || null,
            birthDate: form.birthDate || null,
        };
 
        const formData = new FormData();
 
        formData.append(
            "profile",
            new Blob([JSON.stringify(profilePayload)], {
                type: "application/json",
            })
        );
 
        if (form.iconFile) {
            formData.append("iconFile", form.iconFile);
        }
 
        try {
            const response = await fetch(`${API_BASE_URL}/api/users/${USER_ID}/profile`, {
                method: "PUT",
                body: formData,
            });
 
            if (!response.ok) {
                throw new Error(`プロフィール更新に失敗しました。status=${response.status}`);
            }
 
            successMessage = "プロフィールを保存しました。";
            goto("/profile");
        } catch (error) {
            console.error(error);
            errorMessage = "プロフィールを保存できませんでした。APIのURLやリクエスト形式を確認してください。";
        } finally {
            isSaving = false;
        }
    }
 
    onMount(() => {
        loadProfile();
 
        return () => {
            if (currentObjectUrl) {
                URL.revokeObjectURL(currentObjectUrl);
            }
        };
    });
</script>
 
<div class="min-h-screen w-full overflow-y-auto bg-gray-50 px-4 py-6 sm:px-6">
    <div class="mx-auto max-w-3xl pb-24">
        <div class="mb-5">
            <button
                type="button"
                onclick={() => goto("/profile")}
                class="text-sm font-semibold text-gray-500 transition hover:text-gray-900"
            >
                ← プロフィールに戻る
            </button>
 
            <h1 class="mt-4 text-2xl font-bold text-gray-900">
                プロフィール編集
            </h1>
 
            <p class="mt-2 text-sm text-gray-500">
                アイコンやプロフィール情報を編集できます。
            </p>
        </div>
 
        <form
            class="rounded-2xl border border-gray-200 bg-white shadow-sm"
            onsubmit={(event) => {
                event.preventDefault();
                handleSubmit();
            }}
        >
            <div class="space-y-6 p-5 sm:p-6">
                {#if errorMessage}
                    <div class="rounded-xl bg-red-50 px-4 py-3 text-sm font-semibold text-red-600">
                        {errorMessage}
                    </div>
                {/if}
 
                <section>
                    <h2 class="text-sm font-bold text-gray-900">
                        アイコン 任意
                    </h2>
 
                    <div class="mt-3 flex items-center gap-4">
                        <div class="avatar-box">
                            <img
                                src={displayIconPreviewUrl}
                                alt="プロフィール画像"
                                class="avatar-img"
                                onerror={handleIconPreviewError}
                            />
                        </div>
 
                        <div>
                            <label
                                for="iconFile"
                                class="inline-flex cursor-pointer items-center justify-center rounded-xl bg-gray-100 px-4 py-2 text-sm font-bold text-gray-900 transition hover:bg-gray-200"
                            >
                                画像を選択
                            </label>
 
                            <input
                                id="iconFile"
                                type="file"
                                accept="image/*"
                                onchange={handleIconChange}
                                class="hidden"
                            />
 
                            <p class="mt-2 text-xs text-gray-400">
                                JPG、PNGなどの画像を選択できます。
                            </p>
                        </div>
                    </div>
                </section>
 
                <section>
                    <label
                        for="username"
                        class="text-sm font-bold text-gray-900"
                    >
                        ユーザーネーム 必須
                    </label>
 
                    <input
                        id="username"
                        type="text"
                        bind:value={form.username}
                        placeholder="ユーザーネーム"
                        class="mt-2 w-full rounded-xl border border-gray-200 px-4 py-3 text-sm outline-none transition focus:border-emerald-500"
                    />
                </section>
 
                <section>
                    <h2 class="text-sm font-bold text-gray-900">
                        ロール 必須
                    </h2>
 
                    <div class="mt-3 flex flex-wrap gap-3">
                        {#each roleOptions as role}
                            <button
                                type="button"
                                onclick={() => {
                                    form.role = role.value;
                                }}
                                class={`rounded-xl px-4 py-2 text-sm font-bold transition ${
                                    form.role === role.value
                                        ? "bg-emerald-500 text-white"
                                        : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                                }`}
                            >
                                {role.label}
                            </button>
                        {/each}
                    </div>
                </section>
 
                <section>
                    <label
                        for="email"
                        class="text-sm font-bold text-gray-900"
                    >
                        メールアドレス 必須
                    </label>
 
                    <input
                        id="email"
                        type="email"
                        bind:value={form.email}
                        placeholder="example@example.com"
                        class="mt-2 w-full rounded-xl border border-gray-200 px-4 py-3 text-sm outline-none transition focus:border-emerald-500"
                    />
                </section>
 
                <section>
                    <label
                        for="newPassword"
                        class="text-sm font-bold text-gray-900"
                    >
                        新しいパスワード 任意
                    </label>
 
                    <input
                        id="newPassword"
                        type="password"
                        bind:value={form.newPassword}
                        placeholder="変更する場合のみ入力"
                        autocomplete="new-password"
                        class="mt-2 w-full rounded-xl border border-gray-200 px-4 py-3 text-sm outline-none transition focus:border-emerald-500"
                    />
                </section>
 
                <section>
                    <label
                        for="profileText"
                        class="text-sm font-bold text-gray-900"
                    >
                        自己紹介文 任意
                    </label>
 
                    <textarea
                        id="profileText"
                        bind:value={form.profileText}
                        rows="4"
                        placeholder="自己紹介を入力してください"
                        class="mt-2 w-full resize-none rounded-xl border border-gray-200 px-4 py-3 text-sm outline-none transition focus:border-emerald-500"
                    ></textarea>
                </section>
 
                <section>
                    <h2 class="text-sm font-bold text-gray-900">
                        ジャンル 任意
                    </h2>
 
                    <p class="mt-1 text-xs text-gray-400">
                        複数選択できます。もう一度押すと解除できます。
                    </p>
 
                    <div class="mt-3 flex flex-wrap gap-3">
                        <button
                            type="button"
                            onclick={() => {
                                form.genres = [];
                            }}
                            class={`rounded-xl px-4 py-2 text-sm font-bold transition ${
                                form.genres.length === 0
                                    ? "bg-gray-900 text-white"
                                    : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                            }`}
                        >
                            未設定
                        </button>
 
                        {#each genreOptions as genre}
                            <button
                                type="button"
                                onclick={() => {
                                    toggleGenre(genre.value);
                                }}
                                class={`rounded-xl px-4 py-2 text-sm font-bold transition ${
                                    form.genres.includes(genre.value)
                                        ? "bg-emerald-500 text-white"
                                        : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                                }`}
                            >
                                {genre.label}
                            </button>
                        {/each}
                    </div>
 
                    {#if form.genres.length > 0}
                        <div class="mt-3 flex flex-wrap gap-2">
                            {#each form.genres as selectedGenre}
                                <span class="rounded-full bg-emerald-50 px-3 py-1 text-xs font-bold text-emerald-600">
                                    {selectedGenre}
                                </span>
                            {/each}
                        </div>
                    {/if}
                </section>
 
                <section>
                    <label
                        for="birthDate"
                        class="text-sm font-bold text-gray-900"
                    >
                        生年月日 任意
                    </label>
 
                    <input
                        id="birthDate"
                        type="date"
                        bind:value={form.birthDate}
                        class="mt-2 w-full rounded-xl border border-gray-200 px-4 py-3 text-sm outline-none transition focus:border-emerald-500"
                    />
                </section>
            </div>
 
            <div class="border-t border-gray-100 bg-white p-4 sm:p-5">
                <div class="flex flex-col-reverse gap-3 sm:flex-row sm:justify-end">
                    <button
                        type="submit"
                        class="w-full rounded-xl bg-emerald-500 px-6 py-3 text-sm font-bold text-white transition hover:bg-emerald-600 sm:w-auto"
                    >
                        保存する
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>
 
<style>
    .avatar-box {
        width: 72px;
        height: 72px;
        min-width: 72px;
        min-height: 72px;
        max-width: 72px;
        max-height: 72px;
        overflow: hidden;
        border-radius: 9999px;
        border: 1px solid #e5e7eb;
        background-color: #f3f4f6;
    }
 
    .avatar-img {
        display: block;
        width: 72px;
        height: 72px;
        max-width: 72px;
        max-height: 72px;
        object-fit: cover;
        border-radius: 9999px;
    }
</style>