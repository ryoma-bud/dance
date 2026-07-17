<script>
    let { 
        findEmail = $bindable(), 
        findAuthCode = $bindable(),
        newPassword = $bindable(),
        newPasswordConfirm = $bindable(),

        isFindEmailSent,
        isFindEmailVerified,
        isSendingFindEmail,
        isVerifyingFindCode,
        isResettingPassword,

        findPasswordMessage,
        isFindPasswordError,
        findPasswordErrorMsg,
        findPasswordConfirmErrorMsg,

        sendFindAuthCode,
        verifyFindAuthCode,
        validateFindPassword,
        validateFindPasswordConfirm,
        handleResetPassword,
        showLogin
    } = $props();
</script>

<div class="flex flex-col gap-4">
    <p class="text-xs text-green-100/70 leading-relaxed mb-1">
        ご登録いただいたEmailを入力し、認証を完了してください。<br />
        認証後、新しいパスワードを設定できます。
    </p>

    <!-- Email入力And認証番号発送 -->
    <div class="flex flex-col">
        <label for="find-email" class="mb-2 text-sm text-green-50">Email</label>
        <div class="flex gap-2">
            <input
                id="find-email"
                type="email"
                bind:value={findEmail}
                disabled={isFindEmailSent}
                placeholder="Emailを入力してください。"
                class="flex-1 rounded-2xl border border-lime-400/15 bg-[#030a07]/20 px-4 py-4 text-base text-white outline-none transition placeholder:text-green-200/35 focus:border-lime-400/60 focus:ring-4 focus:ring-lime-400/10 disabled:opacity-50"
            />
            <button
                type="button"
                onclick={sendFindAuthCode}
                disabled={isSendingFindEmail || isFindEmailSent}
                class="rounded-2xl bg-gradient-to-br from-lime-300 to-green-400 px-4 py-4 text-sm font-bold text-[#041006] shadow-md transition hover:-translate-y-0.5 disabled:cursor-not-allowed disabled:opacity-50 whitespace-nowrap"
            >
                {isFindEmailSent ? "送信済み" : isSendingFindEmail ? "送信中..." : "コード送信"}
            </button>
        </div>
    </div>

    <!-- 認証コード入力 -->
    {#if isFindEmailSent && !isFindEmailVerified}
        <div class="flex flex-col mt-2">
            <label for="find-auth-code" class="mb-2 text-sm text-green-50">認証番号</label>
            <div class="flex gap-2">
                <input
                    id="find-auth-code"
                    type="text"
                    bind:value={findAuthCode}
                    placeholder="認証番号 6桁"
                    class="flex-1 rounded-2xl border border-lime-400/15 bg-[#030a07]/20 px-4 py-4 text-base text-white outline-none transition placeholder:text-green-200/35 focus:border-lime-400/60 focus:ring-4 focus:ring-lime-400/10"
                />
                <button
                    type="button"
                    onclick={verifyFindAuthCode}
                    disabled={isVerifyingFindCode}
                    class="rounded-2xl bg-lime-300 px-5 py-4 text-sm font-bold text-[#041006] transition hover:-translate-y-0.5 disabled:cursor-not-allowed disabled:opacity-50"
                >
                    {isVerifyingFindCode ? "確認中..." : "認証確認"}
                </button>
            </div>
        </div>
    {/if}

    <!-- 新しいパスワード入力 -->
    {#if isFindEmailVerified}
        <div class="flex flex-col mt-2 gap-3 border-t border-lime-400/10 pt-4">
            <div class="flex flex-col">
                <label for="new-password" class="mb-2 text-sm text-green-50">新しいパスワード</label>
                <input
                    id="new-password"
                    type="password"
                    bind:value={newPassword}
                    oninput={validateFindPassword}
                    placeholder="新しいパスワード"
                    class="w-full rounded-2xl border border-lime-400/15 bg-[#030a07]/20 px-4 py-4 text-base text-white outline-none transition placeholder:text-green-200/35 focus:border-lime-400/60 focus:ring-4 focus:ring-lime-400/10"
                />
                {#if findPasswordErrorMsg}
                    <p class="text-xs text-red-400 mt-1 pl-1">{findPasswordErrorMsg}</p>
                {/if}
            </div>

            <div class="flex flex-col">
                <label for="new-password-confirm" class="mb-2 text-sm text-green-50">新しいパスワードの確認</label>
                <input
                    id="new-password-confirm"
                    type="password"
                    bind:value={newPasswordConfirm}
                    oninput={validateFindPasswordConfirm}
                    placeholder="もう一度入力してください"
                    class="w-full rounded-2xl border border-lime-400/15 bg-[#030a07]/20 px-4 py-4 text-base text-white outline-none transition placeholder:text-green-200/35 focus:border-lime-400/60 focus:ring-4 focus:ring-lime-400/10"
                />
                {#if findPasswordConfirmErrorMsg}
                    <p class="text-xs text-red-400 mt-1 pl-1">{findPasswordConfirmErrorMsg}</p>
                {/if}
            </div>

            <button
                type="button"
                onclick={handleResetPassword}
                disabled={isResettingPassword || !!findPasswordErrorMsg || !!findPasswordConfirmErrorMsg}
                class="mt-2 w-full rounded-2xl bg-gradient-to-br from-lime-300 to-green-400 px-4 py-4 text-base font-bold text-[#041006] shadow-[0_8px_24px_rgba(124,255,0,0.22)] transition hover:-translate-y-0.5 disabled:cursor-not-allowed disabled:opacity-60"
            >
                {isResettingPassword ? "変更中..." : "パスワードを設定する"}
            </button>
        </div>
    {/if}

    <!-- 状態およびmessage出力 -->
     {#if findPasswordMessage}
        <p class="text-center text-sm {isFindPasswordError ? 'text-red-400' : 'text-lime-300'}">
            {findPasswordMessage}
        </p>
    {/if}

    <div class="relative my-4 text-center">
        <div class="absolute left-0 right-0 top-1/2 h-px -translate-y-1/2 bg-lime-400/10"></div>
        <span class="relative bg-[#08120e99] px-3 text-xs uppercase tracking-[0.08em] text-green-200/50">
            or
        </span>
    </div>

    <div class="text-center">
        <button
            type="button"
            onclick={showLogin}
            class="text-sm font-semibold text-lime-300 transition hover:text-lime-200"
        >
            ログイン画面に戻る
        </button>
    </div>
</div>