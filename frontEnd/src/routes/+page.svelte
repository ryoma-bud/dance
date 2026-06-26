<script>
    import { goto } from "$app/navigation";
    import { authStore } from "../store";

  let isSignup = $state(false);

  // ログイン用状態
  let loginEmail = $state("");
  let loginPassword = $state("");
  let loginMessage = $state("");
  let isLoginError = $state(false);

  // 会員登録用状態
  let signupName = $state("");
  let signupEmail = $state("");
  let signupPassword = $state("");
  let signupPasswordConfirm = $state("");
  let signupMessage = $state("");
  let isSignupError = $state(false);
  let signupRole = $state("USER");

  // メール認証用状態
  let signupAuthCode = $state("");
  let isEmailSent = $state(false);
  let isEmailVerified = $state(false);
  let isSendingEmail = $state(false);
  let isVerifyingCode = $state(false);
  let isSigningUp = $state(false);

  // メッセージ表示用（それぞれ独立して管理）
  let emailMessage = $state("");
  let isEmailError = $state(false);
  
  let codeMessage = $state("");
  let isCodeError = $state(false);

  // 各入力欄ごとライブエラーメッセージ状態
  let nameErrorMsg = $state("");
  let isNamedValid = $state(false);
  /** @type {ReturnType<typeof setTimeout> | undefined} */
  let nameCheckTimeout = undefined;
  let passwordErrorMsg = $state("");
  let passwordConfirmErrorMsg = $state("");
  
  // ライブで有効性を検索するロジック
  // 名前検査
  function validateName() {
    isNamedValid = false;

    // 空欄確認
    if (!signupName) {
      nameErrorMsg = "";

      return;
    }

    const nameRegex = /^(?=.*[a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ])[a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ0-9]+$/;
    
    if (!nameRegex.test(signupName)) {
      nameErrorMsg = "名前には文字が必須です（特殊文字不可）。";
    
      return;
    }

    nameErrorMsg = "重複チェック中";
  
    // サーバに重複チェック(Debounce適用、0.5秒内に入力なければ、サーバに要請)
    clearTimeout(nameCheckTimeout);
    nameCheckTimeout = setTimeout(async () => {
      try {
        const response = await fetch(`http://localhost:8090/api/auth/check-name?name=${encodeURIComponent(signupName)}`);
        const data = await response.json();

        if (response.ok) {
          nameErrorMsg = "";
          isNamedValid = true;
        } else {
          nameErrorMsg = data.message || "すでに使用されている名前です。";
          isNamedValid = false;
        } 
      } catch (error) {
        nameErrorMsg = "サーバと通信できませんでした。";
      }
    }, 500);  // 500ms(0.5秒)待機
  }

  // Passwordを検査する
  function validatePassword() {
    if (!signupPassword) {
      passwordErrorMsg = "";
      return;
    }

    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d).{8,}$/; 
    if (!passwordRegex.test(signupPassword)) {
      passwordErrorMsg = "英語1文字、数字1文字を含め、8文字以上にしてください。";
    } else {
      passwordErrorMsg = "";
    }
    validatePasswordConfirm();  // Passwordが変わったら、確認欄も検査
  }

  function validatePasswordConfirm() {
    if (!signupPasswordConfirm) {
      passwordConfirmErrorMsg = "";
      return;
    }
    if (signupPassword !== signupPasswordConfirm) {
      passwordConfirmErrorMsg = "パスワードが一致していません。";
    } else {
      passwordConfirmErrorMsg = "";
    }
  }

  /**
   * @param {string} selectedRole
   */
  function toggleRole(selectedRole) {
    if (signupRole === selectedRole) {
      signupRole = "USER";
    } else {
      signupRole = selectedRole;
    }
  }

  function clearMessages() {
    loginMessage = "";
    signupMessage = "";
    emailMessage = "";
    codeMessage = "";
    isLoginError = false;
    isSignupError = false;
    isEmailError = false;
    isCodeError = false;
    nameErrorMsg = "";
    passwordErrorMsg = "";
    passwordConfirmErrorMsg = "";
  }

  function showLogin() {
    isSignup = false;
    clearMessages();
  }

  function showSignup() {
    isSignup = true;
    clearMessages();
    isEmailSent = false;
    isEmailVerified = false;
    signupAuthCode = "";
  }

  // ##################################
  // Login
  // ##################################
  async function handleLogin() {
    clearMessages();
    isLoginError = false;
    loginMessage = "ログイン処理中...";
    // ログイン処理をここに実装

    // 入力項目確認
    if (!loginEmail || !loginPassword) {
      isLoginError = true;
      loginMessage = "Emailとパスワードを入力してください。";
    
      return;
    }

    try {
      // BackEndにRequest送る
      const response = await fetch("http://localhost:8090/api/auth/login", {
        method: "POST",
        headers: { "Content-Type" : "application/json" },
        body: JSON.stringify({
          email: loginEmail,
          password: loginPassword,
        }),
      });

      // BNから戻ってきたデータを受け取る
      const data = await response.json();

      console.log("BNからきたデータ:", data);

      // 成功時、処理
      if (response.ok) {
        localStorage.setItem("token", data.token);
        localStorage.setItem("userInfo", JSON.stringify(data.userInfoDto));
        authStore.set(data.userInfoDto);

        loginMessage = "ログインに成功しました。";

        goto("/main");
    } else {
      // 失敗時、処理
      isLoginError = true;
      loginMessage = data.messagae || "Emailまたはパスワードが間違っています。";
    }
  } catch (error) {
    isLoginError = true;
    loginMessage = "サーバと通信できなかったです。";
  }
}

  // ###################################
  // 認証番号送信
  // ###################################
  async function sendAuthCode() {
    clearMessages();
    
    if (!signupEmail) {
      isEmailError = true;
      emailMessage = "Emailを入力してください。";
      return;
    }

    isEmailError = false;
    emailMessage = "認証番号を送信中...";
    isSendingEmail = true;

    try {
      const response = await fetch(
        "http://localhost:8090/api/auth/email/send",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ email: signupEmail }),
        },
      );

      const data = await response.json();

      if (response.ok) {
        isEmailSent = true;
        isEmailError = false;
        emailMessage = data.message || "認証番号を送信しました。メールをご確認ください。";
      } else {
        isEmailError = true;
        emailMessage = data.message || "メール送信に失敗しました。";
      }
    } catch (error) {
      isEmailError = true;
      emailMessage = "サーバと通信できなかったです。";
    } finally {
      isSendingEmail = false;
    }
  }

  // ###################################
  // 認証番号確認
  // ###################################
  async function verifyAuthCode() {
    codeMessage = "";
    
    if (!signupAuthCode) {
      isCodeError = true;
      codeMessage = "認証番号を入力してください。";
      return;
    }

    isVerifyingCode = true;

    try {
      const response = await fetch(
        "http://localhost:8090/api/auth/email/verify",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ email: signupEmail, code: signupAuthCode }),
        },
      );

      const data = await response.json();

      if (response.ok) {
        isEmailVerified = true;
        isCodeError = false;
        codeMessage = data.message || "メール認証が完了しました。";
        // 認証成功したら、メール側のメッセージは消す（すっきりさせるため）
        emailMessage = ""; 
      } else {
        isCodeError = true;
        codeMessage = data.message || "認証番号が間違っているか、期限切れです。";
      }
    } catch (error) {
      isCodeError = true;
      codeMessage = "サーバと通信できなかったです。";
    } finally {
      isVerifyingCode = false;
    }
  }

  // ###########################################
  // 会員登録
  // ###########################################
  async function handleSignup() {
    signupMessage = "";

    // ライブ検証ロジックを一回ずつ実行
    validatePassword();
    validatePasswordConfirm();

    // 空欄チェック
    if (!signupName || !signupEmail || !signupPassword || !signupPasswordConfirm) {
      isSignupError = true;
      signupMessage = "すべての項目を入力してください。";
      
      return;
    }

    if (!isNamedValid) {
      isSignupError = true;
      signupMessage = "名前が重複されてます。他の名前にしてください。"; 

      return;
    }

    // Email認証
    if (!isEmailVerified) {
      isSignupError = true;
      signupMessage = "先にEmail認証を完了してください。";
      
      return;
    }

    isSigningUp = true;

    try {
      const response = await fetch("http://localhost:8090/api/auth/signup", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          name: signupName,
          email: signupEmail,
          password: signupPassword,
        }),
      });

      const data = await response.json();

      if (!response.ok) {
        isSignupError = true;
        signupMessage = data.message || "会員登録に失敗しました。";
        return;
      }

      const registeredEmail = signupEmail;

      // 登録成功後の初期化
      signupName = "";
      signupEmail = "";
      signupPassword = "";
      signupPasswordConfirm = "";
      signupAuthCode = "";
      isEmailSent = false;
      isEmailVerified = false;
      clearMessages();

      // ログイン画面へ切り替え
      isSignup = false;
      loginEmail = registeredEmail;
      loginPassword = "";
      isLoginError = false;
      loginMessage = data.message || "会員登録を完了しました。ログインしてください。";
    } catch (error) {
      isSignupError = true;
      signupMessage = "サーバと通信できなかったです。";
    } finally {
      isSigningUp = false;
    }
  }
</script>

<svelte:head>
  <title>DanStar</title>
  <meta name="viewport" content="width=device-width, initial-scale=1" />
</svelte:head>

<div class="login-bg relative flex min-h-screen items-center justify-center overflow-hidden px-4 py-8 sm:px-6 sm:py-10">
  <div class="absolute inset-0 bg-black/25"></div>

  <section class="relative z-10 mt-10 w-full max-w-[400px] rounded-3xl border border-lime-400/20 bg-[#08120e]/20 p-6 text-white shadow-[0_20px_60px_rgba(0,0,0,0.5),0_0_30px_rgba(57,255,20,0.08)] backdrop-blur-sm sm:mt-12 sm:max-w-[440px] sm:p-7 lg:mt-14 lg:max-w-[480px] lg:p-8">
    <div class="mb-6 text-center">
      <h1 class="text-3xl font-bold tracking-wide text-lime-300">DanStar</h1>
      <p class="mt-2 text-sm text-green-100/70">
        {#if isSignup}
          あなたのダンスを記録するアカウントを作ってみましょう
        {:else}
          Start your Dance with DanStar
        {/if}
      </p>
    </div>

    {#if !isSignup}
      <!-- ================= ログイン画面 ================= -->
      <div class="flex flex-col">
        {#if loginMessage}
          <p class={`mb-4 text-sm ${isLoginError ? 'text-red-400' : 'text-lime-300'}`}>{loginMessage}</p>
        {/if}

        <label for="login-email" class="mb-2 mt-2 text-sm text-green-50">Email</label>
        <input
          id="login-email"
          type="email"
          bind:value={loginEmail}
          onkeydown={(e) => {if (e.key === 'Enter') handleLogin(); }}
          placeholder="Emailを入力してください。"
          class="w-full rounded-2xl border border-lime-400/15 bg-[#030a07]/20 px-4 py-4 text-base text-white outline-none transition placeholder:text-green-200/35 focus:border-lime-400/60 focus:ring-4 focus:ring-lime-400/10"
        />

        <label for="login-password" class="mb-2 mt-4 text-sm text-green-50">パスワード</label>
        <input
          id="login-password"
          type="password"
          bind:value={loginPassword}
          onkeydown={(e) => {if (e.key === 'Enter') handleLogin(); }}
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
    {:else}
      <!-- ================= 会員登録画面 ================= -->
      <div class="flex flex-col">

        <!-- Email入力・送信 -->
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
                isEmailSent = false;
                isEmailVerified = false;
                emailMessage = ""; 
                codeMessage = "";
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
            {:else if isSendingEmail}
              送信中
            {:else if isEmailSent}
              再送信
            {:else}
              認証送信
            {/if}
          </button>
        </div>
        
        <!-- Email欄の下に表示するメッセージ -->
        {#if emailMessage}
          <p class={`mt-2 text-sm ${isEmailError ? 'text-red-400' : 'text-lime-300'}`}>
            {emailMessage}
          </p>
        {/if}

        <!-- 認証番号入力・確認 -->
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
          
          <!-- 認証番号欄の下に表示するメッセージ -->
          {#if codeMessage}
            <p class={`mt-2 text-sm ${isCodeError ? 'text-red-400' : 'text-lime-300'}`}>
              {codeMessage}
            </p>
          {/if}
        {/if}

        <!-- 認証完了時のメッセージ -->
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

        <!-- Role選択領域 -->
        <label class="mb-2 mt-2 text-sm text-green-50">ダンサーおよび主催者の方は選択してください。</label>
          <div class="flex gap-2">  
            <button
              type="button"
              onclick={() => toggleRole("DANCER")}
              class={`flex-1 rounded-2xl border px-2 py-3 text-sm font-bold transition ${signupRole === 'DANCER' ? 'border-lime-400 bg-lime-400/20 text-lime-300' : 'border-lime-400/15 text-green-200/50 hover:border-lime-400/30 hover:text-lime-200'}`}
            >
              ダンサー
            </button>
            <button
              type="button"
              onclick={() => toggleRole("ORGANIZER")}
              class={`flex-1 rounded-2xl border px-2 py-3 text-sm font-bold transition ${signupRole === 'ORGANIZER' ? 'border-lime-400 bg-lime-400/20 text-lime-300' : 'border-lime-400/15 text-green-200/50 hover:border-lime-400/30 hover:text-lime-200'}`}
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

        <!-- 会員登録ボタンの下に表示するメッセージ -->
        {#if signupMessage}
          <p class={`mt-4 text-center text-sm ${isSignupError ? 'text-red-400' : 'text-lime-300'}`}>
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
    {/if}
  </section>
</div>

<style>
  .login-bg {
    background-image: linear-gradient(rgba(0, 0, 0, 0.72), rgba(0, 0, 0, 0.78)),
      url("/images/DanStar.png");
    background-repeat: no-repeat;
    background-position: center 30%;
    background-size: min(980px, 88vw);
    background-color: black;
  }

  @media (max-width: 1366px) {
    .login-bg {
      background-position: center 26%;
      background-size: min(900px, 90vw);
    }
  }

  @media (max-width: 1024px) {
    .login-bg {
      background-position: center 22%;
      background-size: min(820px, 92vw);
    }
  }
</style>