<script lang="ts">
    import { goto } from "$app/navigation";

    const Role = {
        USER: "USER",
        ORGANIZER: "ORGANIZER",
    } as const;

    type Role = (typeof Role)[keyof typeof Role];

    const Genre = {
        HIPHOP: "HIPHOP",
        JAZZ: "JAZZ",
        BREAK: "BREAK",
        LOCK: "LOCK",
        POP: "POP",
        HOUSE: "HOUSE",
        FREESTYLE: "FREESTYLE",
        OTHER: "OTHER",
    } as const;

    type Genre = (typeof Genre)[keyof typeof Genre];

    const roleOptions = [
        { label: "一般ユーザー", value: Role.USER },
        { label: "主催者", value: Role.ORGANIZER },
    ];

    const genreOptions = [
        { label: "HIPHOP", value: Genre.HIPHOP },
        { label: "JAZZ", value: Genre.JAZZ },
        { label: "BREAK", value: Genre.BREAK },
        { label: "LOCK", value: Genre.LOCK },
        { label: "POP", value: Genre.POP },
        { label: "HOUSE", value: Genre.HOUSE },
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
        genre: Genre | "";
        birthDate: string;
    };

    let form = $state<ProfileEditForm>({
        username: "DanStar_User",
        role: Role.USER,
        email: "user@danstar.co.jp",
        newPassword: "",
        profileText: "ダンスが好きです。",
        iconFile: null,
        iconPreviewUrl: "/images/ProfileIcon.png",
        genre: Genre.HIPHOP,
        birthDate: "2000-01-01",
    });

    let errorMessage = $state("");

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

        form.iconFile = file;
        form.iconPreviewUrl = URL.createObjectURL(file);
        errorMessage = "";
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

        errorMessage = "";
        return true;
    }

    function handleSubmit() {
        if (!validateForm()) {
            return;
        }

        const updatePayload = {
            username: form.username,
            role: form.role,
            email: form.email,
            newPassword: form.newPassword || null,
            profileText: form.profileText || null,
            iconFile: form.iconFile,
            genre: form.genre || null,
            birthDate: form.birthDate || null,
        };

        console.log("更新内容", updatePayload);

        goto("/profile");
    }
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
                                src={form.iconPreviewUrl}
                                alt="プロフィール画像"
                                class="avatar-img"
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

                    <div class="mt-3 flex flex-wrap gap-3">
                        <button
                            type="button"
                            onclick={() => {
                                form.genre = "";
                            }}
                            class={`rounded-xl px-4 py-2 text-sm font-bold transition ${
                                form.genre === ""
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
                                    form.genre = genre.value;
                                }}
                                class={`rounded-xl px-4 py-2 text-sm font-bold transition ${
                                    form.genre === genre.value
                                        ? "bg-emerald-500 text-white"
                                        : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                                }`}
                            >
                                {genre.label}
                            </button>
                        {/each}
                    </div>
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
                        type="button"
                        onclick={() => goto("/profile")}
                        class="w-full rounded-xl bg-gray-100 px-6 py-3 text-sm font-bold text-gray-900 transition hover:bg-gray-200 sm:w-auto"
                    >
                        キャンセル
                    </button>

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