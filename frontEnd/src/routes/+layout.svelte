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

    let searchQuery = $state("");

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

    function handleSearch (e: KeyboardEvent) {
        if (e.key === "Enter" && searchQuery.trim()) {
            goto(`/search?q=${encodeURIComponent(searchQuery.trim())}`);
        }
    }
    
</script>

<svelte:head>
    <link rel="icon" href={favicon} />
</svelte:head>

{#if page.url.pathname === "/"}
    {@render children()}
{:else}
    <div class="min-h-dvh w-full bg-[#FAFAFA] text-gray-950 font-sans">
        <!-- 左側のSidebar -->
        <aside class="fixed top-0 left-0 bottom-0 z-40 flex w-64 flex-col overflow-y-auto border-r border-gray-200/80 bg-white text-gray-800 shadow-[2px_0_12px_rgba(0,0,0,0.02)]">
            
            <!-- Logo領域 -->
            <button
                type="button"
                class="flex cursor-pointer justify-center border-b border-gray-100 p-6"
                onclick={() => goto("/main")}
            >
                <img
                    src="/images/DanStarLogo.png"
                    alt="DanStar Logo"
                    class="h-25 w-auto object-contain"
                />
            </button>

            <!-- Menuたち -->
            <nav class="flex-1 px-4 py-6 flex flex-col gap-1 text-sm font-semibold text-gray-600">

                <!-- 検索Tag -->
                <div class="mb-4 px-1">
                    <div class="relative flex items-center">
                        <input
                            type="text"
                            placeholder="Search..."
                            bind:value={searchQuery}
                            onkeydown={handleSearch}
                            class="w-full bg-gray-100 rounded-xl pl-9 pr-3 py-2 text-xs font-normal focus:outline-none focus:ring-2 focus:ring-emerald-500/50 transition"
                        />
                        <svg class="w-4 h-4 text-gray-400 absolute left-3 pointer-events-none" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
                        </svg>
                    </div>
                </div>

                <a href="/dance" class="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-emerald-50 hover:text-emerald-600 transition">
                    <img src="/images/DancTeamIcon.png" alt="Dance Team Icon" class="w-10 h-10 object-contain scale-125" />Dance Team一覧
                </a>
                <a href="/about" class="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-emerald-50 hover:text-emerald-600 transition">
                    <img src="/images/EventIcon.png" alt="Event Icon" class="w-10 h-10 object-contain scale-125" />Event一覧
                </a>
                <a href="/community" class="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-emerald-50 hover:text-emerald-600 transition">
                    <img src="/images/CommunityIcon.png" alt="Community Icon" class="w-10 h-10 object-contain scale-125" />Community
                </a>
                <a href="/follows" class="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-emerald-50 hover:text-emerald-600 transition">
                    <img src="/images/FollowIcon (2).png" alt="Community Icon" class="w-10 h-10 object-contain scale-125" />Follows
                </a>

                <div class="h-px bg-gray-100 my-4"></div>

                <a href="/profile" class="flex items-center gap-3 px-4 py-2 text-xs font-normal hover:text-emerald-600 transition">
                    <img src="/images/ProfileIcon.png" alt="Profile Icon" class="w-4 h-4 object-contain" /> Profile
                </a>

                <button
                    onclick={handleLogout}
                    class="mt-4 flex cursor-pointer items-center gap-3 px-4 py-2 text-left text-xs font-semibold text-rose-500 hover:text-rose-600"
                >
                    <img
                        src="/images/LogoutIcon.png"
                        alt="Logout Icon"
                        class="h-4 w-4 object-contain"
                    />
                    Logout
                </button>
            </nav>

            <!-- 会社情報とポリシー -->
            <div class="border-t border-gray-100 bg-gray-50/50 p-4">
                <button
                    onclick={() => showPolicy = !showPolicy}
                    class="flex w-full items-center justify-between py-2 text-left text-xs font-bold text-emerald-600 transition hover:text-emerald-700"
                >
                    <span class="flex">
                        <img
                            src="/images/CompanyIcon.png"
                            alt="Company Icon"
                            class="ml-4 mr-3 h-5 w-5 object-contain"
                        />
                        会社情報とポリシー
                    </span>

                    <span class="transform text-[10px] transition-transform {showPolicy ? 'rotate-180' : ''}">
                        ▼
                    </span>
                </button>

                {#if showPolicy}
                    <div
                        transition:slide={{ duration: 250 }}
                        class="mt-3 flex flex-col gap-3 border-t border-gray-200/60 pt-3 text-[11px] leading-relaxed text-gray-500"
                    >
                        <div>
                            <p class="font-bold text-gray-700">Call Center</p>
                            <p class="text-xs font-bold text-gray-600">012-345-6789</p>
                            <p>10:00 ~ 17:00 (土日祝休)</p>
                        </div>

                        <hr class="border-gray-200/40" />

                        <div>
                            <p class="font-bold text-gray-700">株式会社DanStar</p>
                            <p>代表：李 仁浩、橋本 竜馬</p>
                            <p>住所：三重県鈴鹿市白子2丁目</p>
                            <p>Email: DanStar@danstar.co.jp</p>
                        </div>

                        <div class="mt-1 flex gap-3 font-semibold text-emerald-600">
                            <a href="/privacy" class="hover:underline">個人情報処理方針</a>
                            <a href="/terms" class="hover:underline">利用規約</a>
                        </div>
                    </div>
                {/if}
            </div>
        </aside>

        <!-- Mainの領域 -->
        <div class="ml-64 min-h-dvh">
            <main class="w-full min-h-dvh">
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