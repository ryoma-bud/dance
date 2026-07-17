<script lang="ts">
    let {
        signupEmail = $bindable(),
        signupAuthCode = $bindable(),
        signupName = $bindable(),
        signupPassword = $bindable(),
        signupPasswordConfirm = $bindable(),
        signupRole = $bindable(),
        isEmailSent,
        isEmailVerified,
        isSendingEmail,
        isVerifyingCode,
        isSigningUp,
        emailMessage,
        isEmailError,
        codeMessage,
        isCodeError,
        nameErrorMsg,
        passwordErrorMsg,
        passwordConfirmErrorMsg,
        signupMessage,
        isSignupError,
        sendAuthCode,
        verifyAuthCode,
        validateName,
        validatePassword,
        validatePasswordConfirm,
        handleSignup,
        showLogin
    } = $props();

    function toggleRole(selectedRole: string) {
        if (signupRole === selectedRole) {
            signupRole = "USER";
        } else {
            signupRole = selectedRole;
        }
    }
</script>

<div class="flex flex-col">
    <label for="signup-email" class="mb-2 mt-4 text-sm text-green-50">Email</label>
    <div class="flex gap-2">
        <input
            id="signup-email"
            type="email"
            bind:value={signupEmail}
            placeholder="Emailを入力してください。"
            disabled={isEmailVerified}
            oninput={() => {
                if (!isEmailVerified) {
                    signupAuthCode = "";
                    // 이메일 변경 시 관련 검증 상태 부모 쪽에서 초기화할 수 있도록 내부에서 값을 비우거나 부모에 알림
                }
            }}
            class="w-full rounded-2xl border border-lime-400/15 bg-[#030a07]/20 px-4 py-4 text-base text-white outline-none transition placeholder:text-green-200/35 disabled:cursor-not-allowed disabled:opacity-60 focus:border-lime-400/60 focus:ring-4 focus:ring-lime-400/10"
        />
        <button
            type="button"
            onclick={sendAuthCode}
            disabled={isSendingEmail || isEmailVerified}
            class="shrink-0 rounded-2xl border border-lime-400/30 px-4 py-4 text-sm font-bold text-lime-300 transition hover:border-lime-300/50 hover:text-lime-200 disabled:cursor-not-allowed disabled:opacity-60"
        >
            {#if isEmailVerified}
                完了
            {:else}
                {#if isSendingEmail}
                    送信中
                {:else if isEmailSent}
                    再送信
                {:else}
                    認証送信
                {/if}
            {/if}
        </button>
    </div>
    
    {#if emailMessage}
        <p class="mt-2 text-sm {isEmailError ? 'text-red-400' : 'text-lime-300'}">
            {emailMessage}
        </p>
    {/if}

    {#if isEmailSent && !isEmailVerified}
        <label for="signup-auth-code" class="mb-2 mt-4 text-sm text-green-50">
            認証番号
        </label>
        <div class="flex gap-2">
            <input
                id="signup-auth-code"
                type="text"
                bind:value={signupAuthCode}
                placeholder="メールで届いた認証番号を入力してください。"
                class="w-full rounded-2xl border border-lime-400/15 bg-[#030a07]/20 px-4 py-4 text-base text-white outline-none transition placeholder:text-green-200/35 focus:border-lime-400/60 focus:ring-4 focus:ring-lime-400/10"
            />
            <button
                type="button"
                onclick={verifyAuthCode}
                disabled={isVerifyingCode}
                class="shrink-0 rounded-2xl border border-lime-400/30 px-4 py-4 text-sm font-bold text-lime-300 transition hover:border-lime-300/50 hover:text-lime-200 disabled:cursor-not-allowed disabled:opacity-60"
            >
                {#if isVerifyingCode}
                    確認中
                {:else}
                    確認
                {/if}
            </button>
        </div>
        
        {#if codeMessage}
            <p class="mt-2 text-sm {isCodeError ? 'text-red-400' : 'text-lime-300'}">
                {codeMessage}
            </p>
        {/if}
    {/if}

    {#if isEmailVerified && codeMessage}
        <p class="mt-2 text-sm text-lime-300">{codeMessage}</p>
    {/if}

    <label for="signup-name" class="mb-2 mt-2 text-sm text-green-50">名前</label>
    <input
        id="signup-name"
        type="text"
        bind:value={signupName}
        oninput={validateName}
        placeholder="DanStarで使う名前を入力してください。"
        class="w-full rounded-2xl border border-lime-400/15 bg-[#030a07]/20 px-4 py-4 text-base text-white outline-none transition placeholder:text-green-200/35 focus:border-lime-400/60 focus:ring-4 focus:ring-lime-400/10"
    />
    {#if nameErrorMsg}
        <p class="mt-1 ml-2 text-xs text-red-400">{nameErrorMsg}</p>
    {/if}

    <label for="signup-password" class="mb-2 mt-4 text-sm text-green-50">
        パスワード
    </label>
    <input
        id="signup-password"
        type="password"
        bind:value={signupPassword}
        oninput={validatePassword}
        placeholder="パスワードを入力してください。"
        class="w-full rounded-2xl border border-lime-400/15 bg-[#030a07]/20 px-4 py-4 text-base text-white outline-none transition placeholder:text-green-200/35 focus:border-lime-400/60 focus:ring-4 focus:ring-lime-400/10"
    />
    {#if passwordErrorMsg}
        <p class="mt-1 ml-2 text-xs text-red-400">{passwordErrorMsg}</p>
    {/if}

    <label for="signup-password-confirm" class="mb-2 mt-4 text-sm text-green-50">
        パスワード再入力
    </label>
    <input
        id="signup-password-confirm"
        type="password"
        bind:value={signupPasswordConfirm}
        oninput={validatePasswordConfirm}
        placeholder="パスワードを再入力してください。"
        class="w-full rounded-2xl border border-lime-400/15 bg-[#030a07]/20 px-4 py-4 text-base text-white outline-none transition placeholder:text-green-200/35 focus:border-lime-400/60 focus:ring-4 focus:ring-lime-400/10"
    />
    {#if passwordConfirmErrorMsg}
        <p class="mt-1 ml-2 text-xs text-red-400">{passwordConfirmErrorMsg}</p>
    {/if}

    <label class="mb-2 mt-2 text-sm text-green-50">ダンサーおよび主催者の方は選択してください。</label>
    <div class="flex gap-2">  
        <button
            type="button"
            onclick={() => toggleRole("DANCER")}
            class="flex-1 rounded-2xl border px-2 py-3 text-sm font-bold transition {signupRole === 'DANCER' ? 'border-lime-400 bg-lime-400/20 text-lime-300' : 'border-lime-400/15 text-green-200/50 hover:border-lime-400/30 hover:text-lime-200'}"
        >
            ダンサー
        </button>
        <button
            type="button"
            onclick={() => toggleRole("ORGANIZER")}
            class="flex-1 rounded-2xl border px-2 py-3 text-sm font-bold transition {signupRole === 'ORGANIZER' ? 'border-lime-400 bg-lime-400/20 text-lime-300' : 'border-lime-400/15 text-green-200/50 hover:border-lime-400/30 hover:text-lime-200'}"
        >
            主催者
        </button>
    </div>  
    <button
        type="button"
        onclick={handleSignup}
        disabled={isSigningUp}
        class="mt-5 w-full rounded-2xl border border-lime-400/30 bg-transparent px-4 py-4 text-base font-bold text-lime-300 shadow-[0_0_18px_rgba(57,255,20,0.08)] transition hover:-translate-y-0.5 hover:border-lime-300/50 hover:text-lime-200 disabled:cursor-not-allowed disabled:opacity-60"
    >
        {#if isSigningUp}
            処理中...
        {:else}
            会員登録
        {/if}
    </button>

    {#if signupMessage}
        <p class="mt-4 text-center text-sm {isSignupError ? 'text-red-400' : 'text-lime-300'}">
            {signupMessage}
        </p>
    {/if}

    <div class="relative my-6 text-center">
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
            もうアカウントをお持ちでしょうか？ログイン画面に戻る
        </button>
    </div>
</div>