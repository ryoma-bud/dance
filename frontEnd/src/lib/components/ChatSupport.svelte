<script lang="ts">
    import { fade, scale } from 'svelte/transition';

    let isOpen = $state(false);      // チャットのToggle状態
    let showTooltip = $state(true);  // 吹き出しの案内状態

    function toggleChat() {
        isOpen = !isOpen;
        if (isOpen) showTooltip = false;
    }
</script>

<div class="fixed bottom-8 right-8 z-50 flex flex-col items-end gap-3 font-sans select-none">
    
    {#if showTooltip && !isOpen}
        <div 
            in:fade={{ duration: 200 }} 
            out:fade={{ duration: 150 }}
            class="relative flex items-center bg-white text-black px-4 py-3 rounded-2xl shadow-[0_10px_30px_rgba(0,0,0,0.3)] border border-gray-100 max-w-[260px]"
        >
            <div class="flex flex-col pr-4">
                <span class="text-xs font-bold text-gray-900 tracking-tight">聞きたい情報はここで聞いてください！</span>
                <span class="text-[10px] text-emerald-600 font-medium mt-0.5 flex items-center gap-1">
                    <span class="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span>
                    数分以内に返信が可能です
                </span>
            </div>
            
            <button 
                onclick={() => showTooltip = false}
                class="text-gray-300 hover:text-gray-500 transition text-xs p-1"
            >
                ✕
            </button>
            
            <div class="absolute bottom-[-5px] right-6 w-2.5 h-2.5 bg-white rotate-45 border-r border-b border-gray-100"></div>
        </div>
    {/if}

    {#if isOpen}
        <div 
            in:scale={{ duration: 200, start: 0.95 }} 
            out:scale={{ duration: 150, start: 0.95 }}
            class="w-80 h-[400px] bg-[#0c1914] border border-lime-400/20 rounded-2xl shadow-[0_25px_50px_-12px_rgba(0,0,0,0.7)] flex flex-col overflow-hidden backdrop-blur-lg mb-2"
        >
            <div class="bg-[#12221a] px-4 py-3 border-b border-lime-400/10 flex items-center justify-between">
                <div class="flex items-center gap-2">
                    <span class="w-2 h-2 rounded-full bg-lime-400 animate-pulse"></span>
                    <span class="text-xs font-bold text-lime-300 tracking-wider">DanStar センター問い合わせ</span>
                </div>
                <button onclick={toggleChat} class="text-gray-400 hover:text-white text-xs">✕</button>
            </div>

            <div class="flex-1 p-4 overflow-y-auto flex flex-col gap-3 text-xs text-gray-200">
                <div class="bg-[#162c22] border border-lime-400/10 p-3 rounded-2xl rounded-tl-none max-w-[85%] leading-relaxed">
                    DanStarに来ていただき、ありがとうございます。 何のヘルプが希望ですか?
                </div>
            </div>

            <div class="p-3 bg-[#07110d] border-t border-lime-400/10 flex gap-2">
                <input 
                    type="text" 
                    placeholder="メッセージを入力してください..." 
                    class="flex-1 bg-[#12221a] border border-lime-400/15 rounded-xl px-3 py-2 text-xs text-white placeholder-gray-500 focus:outline-none focus:border-lime-400/40"
                />
                <button class="bg-lime-400 text-black font-bold px-3 py-1 rounded-xl text-xs hover:bg-lime-300 transition">
                    送信
                </button>
            </div>
        </div>
    {/if}

    <button 
        onclick={toggleChat}
        class="w-14 h-14 rounded-full bg-[#bfff00] hover:bg-[#aee600] active:scale-95 shadow-[0_8px_24px_rgba(191,255,0,0.3)] flex items-center justify-center transition-all group"
    >
        {#if isOpen}
            <span class="text-black text-lg font-bold">✕</span>
        {:else}
            <svg class="w-7 h-7 text-[#08120e] transition-transform group-hover:scale-105" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
            </svg>
        {/if}
    </button>

</div>