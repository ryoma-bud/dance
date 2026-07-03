<script lang="ts">
    // 全域状態のauthStoreだけ持ってくる
    import { authStore } from "../../store";

    // 後でBackendから持ってくる動画リスト
    let videoList = [
        { id: 1, title: "Dance Battle", user: "Dancer_A" },
        { id: 2, title: "Street Dance", user: "Popper_B" },
        { id: 3, title: "雨になってチャレンジ", user: "Choreo_C" },
    ];

    // Following Stories 리스트 데이터
    let storyList = [
        { id: 1, name: "Dancer_A", active: true },
        { id: 2, name: "Popper_B", active: true },
        { id: 3, name: "Locking_J", active: false },
        { id: 4, name: "Bboy_Z", active: true },
        { id: 5, name: "Choreo_C", active: false },
    ];
</script>

<svelte:head>
    <title>DanStar - Main</title>
</svelte:head>

{#if $authStore}
    {@const user = $authStore as any}
    




<div class="w-full h-[100vh] overflow-y-auto snap-y snap-mandatory scroll-smooth flex justify-center bg-[#FAFAFA] no-scrollbar">
        
        <div class="relative w-full max-w-[900px] h-full flex justify-center items-center gap-12 px-4">
            
            <div class="flex flex-col w-full max-w-[480px] h-full justify-center">
                {#each videoList as video}
                    <section class="h-full min-h-[100vh] w-full snap-start flex items-center justify-center py-4 box-border">
                        <article class="relative w-full h-[calc(100vh-40px)] max-h-[860px] aspect-[9/16] bg-black rounded-2xl shadow-[0_8px_32px_rgba(0,0,0,0.12)] overflow-hidden flex flex-col justify-end p-5">
                            
                            <div class="absolute inset-0 bg-gradient-to-b from-black/20 via-transparent to-black/80 flex items-center justify-center -z-10">
                                <span class="text-lime-400/20 text-xs tracking-wider font-bold">DanStar Video Stream Player</span>
                            </div>

                            <!-- いいね、コメントなど -->
                            <div class="absolute right-4 bottom-28 flex flex-col gap-5 items-center z-20">
                                <div class="flex flex-col items-center">
                                    <button class="w-11 h-11 rounded-full bg-black/40 border border-white/10 flex items-center justify-center hover:bg-white/20 text-xl transition shadow-lg backdrop-blur-md active:scale-95">
                                        ❤️
                                    </button>
                                    <span class="text-[10px] text-white font-bold mt-1 shadow-sm">2.4k</span>
                                </div>
                                <div class="flex flex-col items-center">
                                    <button class="w-11 h-11 rounded-full bg-black/40 border border-white/10 flex items-center justify-center hover:bg-white/20 text-lg transition shadow-lg backdrop-blur-md active:scale-95">
                                        💬
                                    </button>
                                    <span class="text-[10px] text-white font-bold mt-1 shadow-sm">142</span>
                                </div>
                                <div class="flex flex-col items-center">
                                    <button class="w-11 h-11 rounded-full bg-black/40 border border-white/10 flex items-center justify-center hover:bg-white/20 text-lg transition shadow-lg backdrop-blur-md active:scale-95">
                                        🔗
                                    </button>
                                    <span class="text-[10px] text-white font-bold mt-1 shadow-sm">공유</span>
                                </div>
                            </div>

                            <!-- 下段の動画の情報欄 -->
                            <div class="flex flex-col gap-1.5 text-white relative z-10 p-2">
                                <h3 class="font-bold text-base text-lime-300 cursor-pointer hover:underline inline-block">
                                    @{video.user}
                                </h3>
                                <p class="text-xs text-gray-100 font-medium leading-relaxed">{video.title}</p>
                                <p class="text-xs text-lime-400 font-semibold tracking-wide">
                                    #Dance #StreetDance #DanStar #Shorts
                                </p>
                            </div>
                        </article>
                    </section>
                {/each}
            </div>

            <!-- 右側のStoryList -->
            <aside class="hidden lg:flex flex-col gap-4 w-56 bg-white border border-gray-200/80 p-5 rounded-2xl shadow-[0_4px_20px_rgba(0,0,0,0.04)] sticky top-1/2 -translate-y-1/2 h-auto max-h-[70vh]">
                <h2 class="text-xs font-black text-gray-900 tracking-wider uppercase border-b border-gray-100 pb-2.5 flex items-center gap-1.5">
                    <span class="w-2 h-2 rounded-full bg-lime-500"></span> Following Stories
                </h2>
                <div class="flex flex-col gap-3.5 overflow-y-auto pr-1">
                    {#each storyList as story}
                        <div class="flex items-center gap-3 cursor-pointer group">
                            <div class="w-10 h-10 rounded-full p-[2px] flex items-center justify-center transition-transform group-hover:scale-105
                                {story.active ? 'bg-gradient-to-tr from-lime-400 to-emerald-400' : 'bg-gray-200'}">
                                <div class="w-full h-full rounded-full bg-white flex items-center justify-center text-xs">
                                    🕺
                                </div>
                            </div>
                            <div class="flex flex-col min-w-0">
                                <span class="text-xs font-bold text-gray-700 truncate group-hover:text-emerald-600 transition">
                                    {story.name}
                                </span>
                                <span class="text-[10px] text-gray-400">
                                    {story.active ? 'Active' : '1h ago'}
                                </span>
                            </div>
                        </div>
                    {/each}
                </div>
            </aside>

        </div>
    </div>

{:else}
    <div class="flex h-screen items-center justify-center bg-[#08120e] text-lime-300 text-lg font-bold tracking-widest animate-pulse">
        Loading...
    </div>
{/if}

<style>
    /* 미관용 스크롤바 숨김 유틸리티 */
    :global(.no-scrollbar::-webkit-scrollbar) {
        display: none;
    }
    :global(.no-scrollbar) {
        -ms-overflow-style: none;
        scrollbar-width: none;
    }
</style>






 