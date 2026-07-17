<script>
    // 外部からもらうProps宣言
    let { 
        loginEmail = $bindable(), 
        loginPassword = $bindable(), 
        loginMessage, 
        isLoginError, 
        handleLogin, 
        showSignup, 
        showFindPassword 
    } = $props();

</script>

<div class="flex flex-col">
    {#if loginMessage}
        <p class="mb-4 text-sm {isLoginError ? 'text-red-400' : 'text-lime-300'}">{loginMessage}</p>
    {/if}

    <!-- Email（ID）入力欄 -->
    <label for="login-email" class="mb-2 mt-2 text-sm text-green-50">Email</label>
    <input 
        id="login-email"
        type="email"
        bind:value={loginEmail}
        onkeydown={(e) => { if (e.key === 'Enter') handleLogin(); }}
        placeholder="Emailを入力してください。"
        class="w-full rounded-2xl border border-lime-400/15 bg-[#030a07]/20 px-4 py-4 text-base text-white outline-none transition placeholder:text-green-200/35 focus:border-lime-400/60 focus:ring-4 focus:ring-lime-400/10"
    />

    <div class="flex justify-between items-center mb-2 mt-4">
        <label for="login-password" class="text-sm text-green-50">パスワード</label>
        <button 
            type="button" 
            onclick={showFindPassword}
            class="text-xs font-semibold text-lime-400/80 transition hover:text-lime-300"
        >
            パスワードをお忘れですか？
        </button>
    </div>

    <input
        id="login-password"
        type="password"
        bind:value={loginPassword}
        onkeydown={(e) => { if (e.key === 'Enter') handleLogin(); }}
        placeholder="パスワードを入力してください"
        class="w-full rounded-2xl border border-lime-400/15 bg-[#030a07]/20 px-4 py-4 text-base text-white outline-none transition placeholder:text-green-200/35 focus:border-lime-400/60 focus:ring-4 focus:ring-lime-400/10"
    />

    <button
        type="button"
        onclick={handleLogin}
        class="mt-5 w-full rounded-2xl bg-gradient-to-br from-lime-300 to-green-400 px-4 py-4 text-base font-bold text-[#041006] shadow-[0_8px_24px_rgba(124,255,0,0.22)] transition hover:-translate-y-0.5"
    >
        Login
    </button>

    <div class="relative my-6 text-center">
        <div class="absolute left-0 right-0 top-1/2 h-px -translate-y-1/2 bg-lime-400/10"></div>
        <span class="relative bg-transparent px-3 text-xs uppercase tracking-[0.08em] text-green-200/50">or</span>
    </div>

    <div class="text-center">
        <button
            type="button"
            onclick={showSignup}
            class="text-sm font-semibold text-lime-300 transition hover:text-lime-200"
        >
            アカウントがないでしょうか？会員登録
        </button>
    </div>

</div>