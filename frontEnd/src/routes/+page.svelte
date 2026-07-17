<script>
    import { goto } from "$app/navigation";
    import { authStore } from "../store";

    // Login, Signup, FindPasswordのComponents
    import LoginSection from "$lib/components/LoginPageComponents/LoginSection.svelte";
    import SignupSection from "$lib/components/LoginPageComponents/SignupSection.svelte";
    import FindPasswordSection from "$lib/components/LoginPageComponents/FindPasswordSection.svelte";

    // 画面操作（会員登録、Password探し）
    let isSignup = $state(false);
    let isFindPassword = $state(false);

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

    // Password探し用の状態
    let findEmail = $state("");
    let findAuthCode = $state("");
    let newPassword = $state("");
    let newPasswordConfirm = $state("");

    let isFindEmailSent = $state(false);
    let isFindEmailVerified = $state(false);
    let isSendingFindEmail = $state(false);
    let isVerifyingFindCode = $state(false);
    let isResettingPassword = $state(false);

    let findPasswordMessage = $state("");
    let isFindPasswordError = $state(false);
    let isFindingPassword = $state(false);

    let findPasswordErrorMsg = $state("");
    let findPasswordConfirmErrorMsg = $state("");

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
  
    // ライブで`有効性を検索するロジック
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
                const response = await fetch(`http://localhost:8080/api/auth/check-name?name=${encodeURIComponent(signupName)}`);
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

    // Password探し用のPassword検査
    function validateFindPassword() {
        if (!newPassword) {
            findPasswordErrorMsg = "";
            return;
        }

        const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d).{8,}$/; 
        if (!passwordRegex.test(newPassword)) {
            findPasswordErrorMsg = "英語1文字、数字1文字を含め、8文字以上にしてください。";
        } else {
            findPasswordErrorMsg = "";
        }
        validateFindPasswordConfirm();
    }

    function validateFindPasswordConfirm() {
        if (!newPasswordConfirm) {
            findPasswordConfirmErrorMsg = "";
            return;
        }
        if (newPassword !== newPasswordConfirm) {
            findPasswordConfirmErrorMsg = "パスワードが一致していません。";
        } else {
            findPasswordConfirmErrorMsg = "";
        }
    }

    // /**
    //  * @param {string} selectedRole
    //  */
    // function toggleRole(selectedRole) {
    //     if (signupRole === selectedRole) {
    //         signupRole = "USER";
    //     } else {
    //         signupRole = selectedRole;
    //     }
    // }

    function clearMessages() {
        loginMessage = "";
        signupMessage = "";
        emailMessage = "";
        codeMessage = "";
        findPasswordMessage = "";
        isLoginError = false;
        isSignupError = false;
        isEmailError = false;
        isCodeError = false;
        isFindPasswordError = false;
        nameErrorMsg = "";
        passwordErrorMsg = "";
        passwordConfirmErrorMsg = "";
        findPasswordErrorMsg = "";
        findPasswordConfirmErrorMsg = "";
    }

    function showFindPassword() {
        isSignup = false;
        isFindPassword = true;
        isFindEmailSent = false;
        isFindEmailVerified = false;
        findEmail = "";
        findAuthCode = "";
        newPassword = "";
        newPasswordConfirm = "";
        clearMessages();
    }

    function showLogin() {
        isSignup = false;
        isFindPassword = false;
        clearMessages();
    }

    function showSignup() {
        isSignup = true;
        isFindPassword = false;
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
            const response = await fetch("http://localhost:8080/api/auth/login", {
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
                loginMessage = data.message || "Emailまたはパスワードが間違っています。"; // data.messagae 오타 수정
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
                "http://localhost:8080/api/auth/email/send",
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
                "http://localhost:8080/api/auth/email/verify",
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
            const response = await fetch("http://localhost:8080/api/auth/signup", {
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

    // #################################
    // Find Password
    // #################################
    async function sendFindAuthCode() {
        clearMessages();
        if (!findEmail) {
            isFindPasswordError = true;
            findPasswordMessage = "Emailを入力してください。";
            return;
        }

        isSendingFindEmail = true;
        findPasswordMessage = "認証番号を送信中...";

        try {
            const response = await fetch("http://localhost:8080/api/auth/password/email/send", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email: findEmail }),
            });

            const data = await response.json();

            if (response.ok) {
                isFindEmailSent = true;
                isFindPasswordError = false;
                findPasswordMessage = data.message || "認証番号を送信しました。メールをご確認ください。";
            } else {
                isFindPasswordError = true;
                findPasswordMessage = data.message || "送信に失敗しました。";
            }
        } catch (error) {
            isFindPasswordError = true;
            findPasswordMessage = "サーバと通信できませんでした。";
        } finally {
            isSendingFindEmail = false;
        }
    }

    async function verifyFindAuthCode() {
        clearMessages();
        if (!findAuthCode) {
            isFindPasswordError = true;
            findPasswordMessage = "認証番号を入力してください。";
            return;
        }

        isVerifyingFindCode = true;

        try {
            const response = await fetch("http://localhost:8080/api/auth/email/verify", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email: findEmail, code: findAuthCode }),
            });

            const data = await response.json();

            if (response.ok) {
                isFindEmailVerified = true;
                isFindPasswordError = false;
                findPasswordMessage = "メール認証が完了しました。新しいパスワードを設定してください。";
            } else {
                isFindPasswordError = true;
                findPasswordMessage = data.message || "認証番号が間違っているか、期限切れです。";
            }
        } catch (error) {
            isFindPasswordError = true;
            findPasswordMessage = "サーバと通信できませんでした。";
        } finally {
            isVerifyingFindCode = false;
        }
    }

    async function handleResetPassword() {
        clearMessages();
        validateFindPassword();
        validateFindPasswordConfirm();

        if (!newPassword || !newPasswordConfirm) {
            isFindPasswordError = true;
            findPasswordMessage = "新しいパスワードを入力してください。";
            return;
        }

        if (findPasswordErrorMsg || findPasswordConfirmErrorMsg) {
            isFindPasswordError = true;
            findPasswordMessage = "パスワードの入力を確認してください。";
            return;
        }

        isResettingPassword = true;

        try {
            const response = await fetch("http://localhost:8080/api/auth/password/reset", {
                method: "POST",
                headers: { "Content-Type" : "application/json" },
                body: JSON.stringify({ 
                    email: findEmail, 
                    newPassword: newPassword 
                }),
            });

            const data = await response.json();
            if (response.ok) {
                isFindPassword = false;
                loginEmail = findEmail;
                loginPassword = "";
                isLoginError = false;
                loginMessage = "パスワード再設定が完了しました。新しいパスワードでログインしてください。";
            } else {
                isFindPasswordError = true;
                findPasswordMessage = data.message || "パスワード再設定に失敗しました。";
            }
        } catch (error) {
            isFindPasswordError = true;
            findPasswordMessage = "サーバと通信できませんでした。";
        } finally {
            isResettingPassword = false;
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
        <div class="mb-6 text-center flex items-center flex-col">
            <!-- <h1 class="text-3xl font-bold tracking-wide text-lime-300">DanStar</h1> -->
            <img 
                src="/images/DanStarLogo.png"
                alt="Danstar Logo"
                class="h-40 w-auto object-contain"
            />
            <p class="mt-2 text-sm text-green-100/70">
                {#if isFindPassword}
                    パスワードをお忘れですか？
                {:else if isSignup}
                    あなたのダンスを記録するアカウントを作ってみましょう
                {:else}
                    Start your Dance with DanStar
                {/if}
            </p>
        </div>

        <!-- Component組み立てるところ -->
        {#if !isSignup && !isFindPassword}
            <!-- LoginSection -->
            <LoginSection 
                bind:loginEmail 
                bind:loginPassword 
                {loginMessage} 
                {isLoginError} 
                {handleLogin} 
                {showSignup} 
                {showFindPassword} 
            />
        
        {:else if isFindPassword}
            <!-- FindPasswordSection -->
            <FindPasswordSection 
                bind:findEmail={findEmail}
                bind:findAuthCode={findAuthCode}
                bind:newPassword={newPassword}
                bind:newPasswordConfirm={newPasswordConfirm}
                
                {isFindEmailSent}
                {isFindEmailVerified}
                {isSendingFindEmail}
                {isVerifyingFindCode}
                {isResettingPassword}

                {findPasswordMessage}
                {isFindPasswordError}
                {findPasswordErrorMsg}
                {findPasswordConfirmErrorMsg}

                {sendFindAuthCode}
                {verifyFindAuthCode}
                {validateFindPassword}
                {validateFindPasswordConfirm}
                {handleResetPassword}
                {showLogin}
            />

        {:else}
            <!-- SignupSection -->
            <SignupSection 
                bind:signupEmail
                bind:signupAuthCode
                bind:signupName
                bind:signupPassword
                bind:signupPasswordConfirm
                bind:signupRole
                {isEmailSent} 
                {isEmailVerified} 
                {isSendingEmail} 
                {isVerifyingCode} 
                {isSigningUp}
                {emailMessage} 
                {isEmailError} 
                {codeMessage} 
                {isCodeError}
                {nameErrorMsg} 
                {passwordErrorMsg} 
                {passwordConfirmErrorMsg}
                {signupMessage}
                {isSignupError}
                {sendAuthCode} 
                {verifyAuthCode} 
                {validateName} 
                {validatePassword} 
                {validatePasswordConfirm} 
                {handleSignup} 
                {showLogin}
            />

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