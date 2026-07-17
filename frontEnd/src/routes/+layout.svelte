HTML
<script lang="ts">
    import "./layout.css";
    import favicon from "$lib/assets/favicon.svg";
    import { page } from "$app/state"; 
    import { onMount } from "svelte";
    import { goto } from "$app/navigation";
    import { authStore } from "../store";
    import ChatSupport from "$lib/components/ChatSupport.svelte";
    import { slide } from "svelte/transition";

    let { children } = $props();

    // 会社情報とポリシー
    let showPolicy = $state(false);

    onMount(() => {
        const storedUserInfo = localStorage.getItem("userInfo");
        if (storedUserInfo) {
            authStore.set(JSON.parse(storedUserInfo));
        } else {
            if (page.url.pathname !== "/") {
                alert("ログインが必要です。");
                goto("/");
            }
        }
    });

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
    <div class="h-screen w-screen bg-[#FAFAFA] text-gray-950 font-sans flex overflow-hidden">
        
        <!-- 左側のSidebar -->
        <aside class="fixed top-0 left-0 bottom-0 w-64 bg-white border-r border-gray-200/80 text-gray-800 flex flex-col z-40 overflow-y-auto shadow-[2px_0_12px_rgba(0,0,0,0.02)]">
            <!-- Logo領域 -->
            <div class="p-6 flex justify-center border-b border-gray-100 cursor-pointer" onclick={() => goto("/main")}>
                <img src="/images/DanStarLogo.png" alt="DanStar Logo" class="h-25 w-auto object-contain" />
            </div>

            <!-- Menuたち -->
            <nav class="flex-1 px-4 py-6 flex flex-col gap-1 text-sm font-semibold text-gray-600">
                <a href="/dance" class="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-emerald-50 hover:text-emerald-600 transition">
                    <img src="/images/DancTeamIcon.png" alt="Dance Team Icon" class="w-10 h-10 object-contain scale-125" />Dance Team一覧
                </a>
                <a href="/about" class="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-emerald-50 hover:text-emerald-600 transition">
                    <img src="/images/EventIcon.png" alt="Event Icon" class="w-10 h-10 object-contain scale-125" />Event一覧
                </a>
                <a href="/community" class="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-emerald-50 hover:text-emerald-600 transition">
                    <img src="/images/CommunityIcon.png" alt="Community Icon" class="w-10 h-10 object-contain scale-125" />Community
                </a>
                
                <div class="h-[1px] bg-gray-100 my-4"></div>

                <!-- <a href="/main" class="flex items-center gap-3 px-4 py-2 text-xs font-normal hover:text-emerald-600 transition">
                    <img src="/images/UploadIcon.png" alt="Upload Icon" class="w-4 h-4 object-contain"/>Upload
                </a> -->
                <a href="/profile" class="flex items-center gap-3 px-4 py-2 text-xs font-normal hover:text-emerald-600 transition">
                    <img src="/images/ProfileIcon.png" alt="Profile Icon" class="w-4 h-4 object-contain" /> Profile
                </a>

                <button onclick={handleLogout} class="flex items-center gap-3 px-4 py-2 text-xs font-semibold text-rose-500 hover:text-rose-600 cursor-pointer text-left mt-4">
                    <img src="/images/LogoutIcon.png" alt="Logout Icon" class="w-4 h-4 object-contain" />Logout
                </button>
            </nav>

            <!-- 会社情報とポリシー -->
            <div class="p-4 border-t border-gray-100 bg-gray-50/50">
                <button 
                    onclick={() => showPolicy = !showPolicy}
                    class="w-full text-left text-xs font-bold text-emerald-600 flex items-center justify-between py-2 hover:text-emerald-700 transition"
                >
                    <span class="flex"> 
                        <img src="/images/CompanyIcon.png" alt="Company Icon" class="w-5 h-5 object-contain ml-4 mr-3"/>会社情報とポリシー
                    </span>
                    <span class="text-[10px] transform transition-transform {showPolicy ? 'rotate-180' : ''}">▼</span>
                </button>

                {#if showPolicy}
                    <div transition:slide={{ duration: 250 }} class="mt-3 flex flex-col gap-3 text-[11px] text-gray-500 leading-relaxed border-t border-gray-200/60 pt-3">
                        <div>
                            <p class="font-bold text-gray-700">Call Center</p>
                            <p class="text-gary-600 text-xs font-bold">012-345-6789</p>
                            <p>10:00 ~ 17:00 (土日祝休)</p>
                        </div>
                        <hr class="border-gray-200/40"/>
                        <div>
                            <p class="font-bold text-gray-700">株式会社DanStar</p>
                            <p>代表：李 仁浩、橋本 竜馬</p>
                            <p>住所：三重県鈴鹿市白子2丁目</p>
                            <p>Email: DanStar@danstar.co.jp</p>
                        </div>
                        <div class="flex gap-3 text-emerald-600 font-semibold mt-1">
                            <a href="/privacy" class="hover:underline">個人情報処理方針</a>
                            <a href="/terms" class="hover:underline">利用規約</a>
                        </div>
                    </div>
                {/if}
            </div>
        </aside>

        <!-- Mainの領域（Sidebarの広さであるwidht64ほど確報） -->
        <div class="flex-1 ml-64 h-full relative flex flex-col overflow-hidden">


            <main class="flex-1 w-full h-full">
                {@render children()}
            </main>
            
            <ChatSupport />
        </div>
    </div>
{/if}


<style>

    aside::-webkit-scrollbar {
        display: none;
    }

    aside {
        -ms-overflow-style: none; /* IE and Edge */
        scrollbar-width: none; /* Firefox */
    }

</style>