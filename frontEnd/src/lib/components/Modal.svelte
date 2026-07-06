<script lang="ts">
    import { fade, scale } from "svelte/transition";
    
    let {
        isOpen = $bindable(false), 
        type = 'alert',
        title = '',
        message = '',
        confirmText = '確認',
        cancelText = 'キャンセル',
        onConfirm = () => {}, 
        onCancel = () => {}
    } = $props<{
        isOpen: boolean;
        type?: 'alert' | 'confirm';
        title?: string;
        message?: string;
        confirmText?: string;
        cancelText?: string;
        onConfirm?: () => void;
        onCancel?: () => void;
    }>();

    function handleConfirm() {
        onConfirm();
        close();
    }

    function handleCancel() {
        onCancel();
        close();
    }

    function close() {
        isOpen = false;
    }

    function handleKeydown(event: KeyboardEvent) {
        if (event.key === 'Escape' && isOpen) {
            handleCancel();
        }
    }
</script>

<svelte:window onkeydown={handleKeydown} />

{#if isOpen}
    <div 
        class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/40 backdrop-blur-[3px]"
        transition:fade={{ duration: 150 }}
        onclick={(e) => { if (e.target === e.currentTarget) handleCancel(); }}
        role="none"
    >
        <div
            class="w-full max-w-sm bg-white border border-gray-100 rounded-2xl p-6 shadow-[0_12px_40px_rgba(0,0,0,0.12)] flex flex-col gap-4 text-center select-none"
            transition:scale={{ start: 0.95, duration: 150 }}
            role="dialog"
            aria-modal="true"
            tabindex="0" 
        >

            <div class="relative flex items-center min-h-8 pb-1">
                <div class="absolute left-0 flex items-center">
                    <img src="/images/DanStarLogo.png" alt="DanStar Logo" class="h-10 w-auto object-contain"/>
                </div>
            
                {#if title}
                    <h3 class="w-full text-lg font-extrabold text-gray-900 tracking-tight text-center px-16"> 
                        {title}
                    </h3>
                {/if}
            </div>

            <p class="text-sm text-gray-600 leading-relaxed whitespace-pre-line text-center">
                {message}
            </p>

            <div class="flex gap-2 mt-2 w-full">
                {#if type === 'confirm'}
                    <button
                        type="button"
                        class="flex-1 py-3 text-sm font-semibold text-gray-500 bg-gray-50 hover:bg-gray-100 rounded-xl transition-all duration-150 cursor-pointer border border-gray-200/50 outline-none"                
                        onclick={handleCancel}
                    >   
                        {cancelText}
                    </button>
                {/if}

                <button
                    type="button"
                    class="flex-1 py-3 text-sm font-bold bg-linear-to-r from-[#9EFF00] to-[#7EDF00] hover:brightness-105 active:scale-[0.98] rounded-xl transition-all duration-150 shadow-[0_4px_12px_rgba(158,255,0,0.3)] text-black cursor-pointer border-none outline-none"
                    onclick={handleConfirm}
                >
                    {confirmText}
                </button>
            </div>
        </div>
    </div>
{/if}