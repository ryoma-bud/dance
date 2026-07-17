<script lang="ts">
    import Modal from '$lib/components/Modal.svelte';

    let isAlertOpen = $state(false);
    let isConfirmOpen = $state(false);

    function handleConfirmAction() {
        console.log('確認ボタンを押す');
    }

    function handleCancelAction() {
        console.log('キャンセルボタンを押す');
    }
</script>

<div class="p-8 flex gap-4">
    <button onclick={() => isAlertOpen = true} class="px-4 py-2 bg-emerald-600 text-white rounded">
        Alert Modal
    </button>
    <button onclick={() => isConfirmOpen = true} class="px-4 py-2 bg-zinc-800 text-white rounded">
        Confirm Modal
    </button>
</div>

<!-- 1. Alert Modal -->
<Modal 
    bind:isOpen={isAlertOpen}
    type="alert"
    title="お知らせ"
    message="保存が完了しました。"
    confirmText="確認"
/>

<!-- 2. Confirm Modal -->
<Modal 
    bind:isOpen={isConfirmOpen}
    type="confirm"
    title="ログアウト"
    message="本当にログアウトしますか？"
    confirmText="はい"
    cancelText="いいえ"
    onConfirm={handleConfirmAction}
    onCancel={handleCancelAction}
/>