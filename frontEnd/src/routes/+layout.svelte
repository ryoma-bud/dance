<script lang="ts">
    import "./layout.css";
    import favicon from "$lib/assets/favicon.svg";
    import { page } from "$app/state"; 
    import { onMount } from "svelte";
    import { goto } from "$app/navigation";
    import { authStore } from "../store";
    import ChatSupport from "$lib/components/ChatSupport.svelte";

    let { children } = $props();

    onMount(() => {
        // アプリが実行する時に、一回だけローカルStorage確認
        const storedUserInfo = localStorage.getItem("userInfo");

        if (storedUserInfo) {
            // 情報があったら全域StoreにSetting
            authStore.set(JSON.parse(storedUserInfo));
        } else {
            // 情報が無いのにログイン画面ではなかったら、ログイン画面に行かせる
            if (page.url.pathname !== "/") {
                alert("ログインが必要です。");
                goto("/");
            }
        }
    });

    // Logout
    function handleLogout() {
        localStorage.removeItem("token");
        localStorage.removeItem("userInfo");
        authStore.set(null);
        goto("/");
    }
</script>

<svelte:head>
    <link rel="icon" href={favicon} />
</svelte:head>

{#if page.url.pathname === "/"}
    {@render children()}
{:else}
    <div class="global-bg min-h-screen flex flex-col text-white font-sans">
        
        <header class="fixed top-0 left-0 right-0 h-16 bg-[#08120e]/95 backdrop-blur-md border-b border-lime-400/10 z-50 flex items-center px-8 relative">
            
            <div class="flex items-center gap-6 z-10">
                <div class="text-2xl font-bold tracking-wider text-lime-300 cursor-pointer" onclick={() => goto("/main")}>
                    DanStar
                </div>
            </div>

            <div class="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 max-w-xs w-full hidden md:block z-20">
                <input 
                    type="text" 
                    placeholder="🔍 Search" 
                    class="w-full bg-[#12221a] border border-lime-400/20 rounded-lg px-3 py-1.5 text-sm text-white placeholder-gray-500 focus:outline-none focus:border-lime-400/50 transition"
                />
            </div>

            <nav class="ml-auto flex items-center gap-6 text-sm font-semibold text-green-100/80 z-10">
                <a href="/dance" class="hover:text-lime-300 transition">Dance Team一覧</a>
                <a href="/about" class="hover:text-lime-300 transition">Event一覧</a>
                <a href="/community" class="hover:text-lime-300 transition">Community</a>
                
                <div class="h-4 w-[1px] bg-lime-400/20 hidden sm:block"></div>

                <a href="/main" class="hidden sm:flex items-center gap-1 hover:text-lime-300 transition text-xs font-normal">
                    <span>➕</span> Upload
                </a>
                <a href="/main" class="hidden sm:flex items-center gap-1 hover:text-lime-300 transition text-xs font-normal">
                    <span>👤</span> Profile
                </a>

                <button
                    onclick={handleLogout}
                    class="text-sm font-semibold text-lime-300 cursor-pointer hover:text-lime-200 ml-2"
                >
                    Logout
                </button>
            </nav>
        </header>

        <div class="flex-1 w-full pt-16">
            {@render children()}
        </div>

        <footer class="w-full bg-[#08120e]/95 border-t border-lime-400/10 py-12 px-6 z-30 relative text-gray-400">
            <div class="max-w-7xl mx-auto grid grid-cols-1 md:grid-cols-3 gap-8 md:gap-12 text-sm">
                
                <div class="flex flex-col gap-2 md:pl-20">
                    <h2 class="text-xl font-bold text-lime-400 mb-1">Call Center</h2>
                    <p class="text-2xl font-bold text-white tracking-wide">012-345-6789</p>
                    <div class="mt-1 space-y-1 text-gray-300">
                        <p>勤務時間： AM 10:00 ~ PM 05:00</p>
                        <p class="text-gray-500 text-xs">土日および祝日は休ませていただきます。</p>
                    </div>
                </div>

                <div class="flex flex-col gap-2 md:items-center md:text-center">
                    <div class="flex flex-col gap-2 items-start md:items-center text-left md:text-center">
                        <h2 class="text-xl font-bold text-lime-400 mb-1">DanStar（ダンスター）</h2>
                        <p class="text-white font-medium">株式会社DanStar</p>
                        <p>代表者：李 仁浩、橋本 竜馬</p>
                        <p>住所：三重県鈴鹿市白子2丁目</p>
                        <p>Email: DanStar@danstar.co.jp</p>
                    </div>
                </div>

                <div class="flex flex-col justify-between items-start md:items-end text-left md:text-right min-h-[120px] md:pr-30">
                    <div class="flex md:flex-col gap-6 md:gap-2">
                        <a href="/privacy" class="text-lime-300 font-semibold hover:underline hover:text-lime-200 transition text-xs tracking-wider">
                            個人情報処理方針
                        </a>
                        <a href="/terms" class="text-lime-300 font-semibold hover:underline hover:text-lime-200 transition text-xs tracking-wider">
                            利用規約
                        </a>
                    </div>
            
                    <div class="hidden md:block h-8"></div>
                </div>


            </div>
        </footer>

        <ChatSupport />
    </div>
{/if}

<style>

    .global-bg {
        background-image: linear-gradient(rgba(0, 0, 0, 0.75), rgba(0, 0, 0, 0.88)),
            url("/images/OnlyBackground.png");
        background-repeat: no-repeat;
        background-position: center center;
        background-size: cover;
        background-attachment: fixed;
        background-color: black;
    }
</style>