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
    
    <!-- <div class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 pointer-events-none z-0">
        <h1 class="text-[10vw] font-black text-lime-400/5 select-none tracking-widest uppercase">
            DanStar
        </h1>
    </div> -->

    <div class="relative z-10 max-w-[850px] mx-auto px-4 py-10 flex gap-8 justify-center items-start">
        
        <aside class="hidden md:block w-56 sticky top-24 flex flex-col gap-4 bg-black/50 p-4 rounded-2xl border border-lime-400/10 backdrop-blur-md shadow-lg">
            <h2 class="text-xs font-bold text-lime-400 tracking-wider uppercase border-b border-lime-400/10 pb-2">
                Following Stories
            </h2>
            <div class="flex flex-col gap-3.5 overflow-y-auto max-h-[65vh] pr-1">
                {#each storyList as story}
                    <div class="flex items-center gap-3 cursor-pointer group">
                        <div class="w-10 h-10 rounded-full p-[2px] flex items-center justify-center transition-transform group-hover:scale-105
                            {story.active ? 'bg-gradient-to-tr from-lime-500 to-green-300' : 'bg-gray-700'}">
                            <div class="w-full h-full rounded-full bg-[#0d1f18] flex items-center justify-center text-xs">
                                🕺
                            </div>
                        </div>
                        <div class="flex flex-col min-w-0">
                            <span class="text-xs font-semibold text-gray-200 truncate group-hover:text-lime-300 transition">
                                {story.name}
                            </span>
                            <span class="text-[10px] text-gray-500">
                                {story.active ? 'Active' : '1h ago'}
                            </span>
                        </div>
                    </div>
                {/each}
            </div>
        </aside>

        <main class="flex-1 flex flex-col items-center gap-10 w-full max-w-[380px]">
            
            <div class="w-full text-center text-xs text-lime-300 font-bold bg-black/60 px-4 py-2.5 rounded-xl backdrop-blur-sm border border-lime-400/10">
                Welcome to DanStar, {user.name}様！
            </div>

            <div class="flex flex-col gap-14 w-full">
                {#each videoList as video}
                    <article class="relative w-full aspect-[9/16] bg-black/80 rounded-2xl border border-lime-400/10 shadow-[0_15px_35px_rgba(0,0,0,0.6)] overflow-hidden flex flex-col justify-end p-5 backdrop-blur-sm">
                        
                        <div class="absolute inset-0 bg-gradient-to-b from-transparent via-black/10 to-black/90 flex items-center justify-center -z-10">
                            <span class="text-lime-300/10 text-xs tracking-wider">Video Player ({video.id})</span>
                        </div>

                        <div class="absolute right-3 bottom-24 flex flex-col gap-5 items-center z-20">
                            <button class="w-10 h-10 rounded-full bg-black/50 border border-lime-400/20 flex items-center justify-center hover:bg-lime-400 hover:text-black transition-all text-lime-300 shadow-md active:scale-95">
                                ❤️
                            </button>
                            <button class="w-10 h-10 rounded-full bg-black/50 border border-lime-400/20 flex items-center justify-center hover:bg-lime-400 hover:text-black transition-all text-xs font-bold text-lime-300 shadow-md active:scale-95">
                                💬
                            </button>
                            <button class="w-10 h-10 rounded-full bg-black/50 border border-lime-400/20 flex items-center justify-center hover:bg-lime-400 hover:text-black transition-all text-lime-300 shadow-md active:scale-95">
                                🔗
                            </button>
                        </div>

                        <div class="flex flex-col gap-1.5 text-white relative z-10 bg-gradient-to-t from-black/95 via-black/30 to-transparent -mx-5 -mb-5 p-5 pt-8 rounded-b-2xl">
                            <h3 class="font-bold text-sm text-lime-300 cursor-pointer hover:underline inline-block">
                                @{video.user}
                            </h3>
                            <p class="text-xs text-gray-200 font-medium">{video.title}</p>
                            <p class="text-[11px] text-lime-400/60 font-semibold tracking-wide">
                                #Dance #StreetDance #DanStar
                            </p>
                        </div>
                    </article>
                {/each}
            </div>
        </main>
    </div>

{:else}
    <div class="flex min-h-[80vh] items-center justify-center text-lime-300 text-lg font-bold tracking-widest animate-pulse">
        Loading...
    </div>
{/if}