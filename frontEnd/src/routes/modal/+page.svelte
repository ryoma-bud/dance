<script lang="ts">
    import Modal from '$lib/components/Modal.svelte';

    let isAlertOpen = $state(false);
    let isConfirmOpen = $state(false);

    function handleConfirmAction() {
        console.log('확인 버튼을 누름');
    }

    function handleCancelAction() {
        console.log('취소 버튼을 누름');
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

<!-- 1. 단순 확인용 알림 -->
<Modal 
    bind:isOpen={isAlertOpen}
    type="alert"
    title="お知らせ"
    message="保存が完了しました。"
    confirmText="確認"
/>

<!-- 2. Yes / No 선택 모달 -->
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