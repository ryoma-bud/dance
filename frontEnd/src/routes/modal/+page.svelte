<script lang="ts">
    import Modal from "$lib/components/Modal.svelte";

    type ModalStatus = "default" | "loading" | "success" | "error";

    let isAlertOpen = $state(false);
    let isConfirmOpen = $state(false);
    let confirmModalStatus = $state<ModalStatus>("default");

    function sleep(ms: number) {
        return new Promise((resolve) => setTimeout(resolve, ms));
    }

    function openConfirmModal() {
        confirmModalStatus = "default";
        isConfirmOpen = true;
    }

    async function handleConfirmAction() {
        console.log("確認ボタンを押す");

        confirmModalStatus = "loading";

        try {
            await sleep(1200);

            confirmModalStatus = "success";
        } catch (error) {
            console.error(error);
            confirmModalStatus = "error";
        }
    }

    function handleCancelAction() {
        console.log("キャンセルボタンを押す");
        confirmModalStatus = "default";
    }

    function handleConfirmClose() {
        confirmModalStatus = "default";
    }
</script>

<div class="flex gap-4 p-8">
    <button
        onclick={() => (isAlertOpen = true)}
        class="rounded bg-emerald-600 px-4 py-2 text-white"
    >
        Alert Modal
    </button>

    <button
        onclick={openConfirmModal}
        class="rounded bg-zinc-800 px-4 py-2 text-white"
    >
        Confirm Modal
    </button>
</div>

<Modal
    bind:isOpen={isAlertOpen}
    type="alert"
    status="success"
    successTitle="保存完了"
    successMessage="保存が完了しました。"
    confirmText="確認"
/>

<Modal
    bind:isOpen={isConfirmOpen}
    type="confirm"
    status={confirmModalStatus}
    title="ログアウト"
    message="本当にログアウトしますか？"
    confirmText="はい"
    cancelText="いいえ"
    loadingText="ログアウト処理中です..."
    successTitle="ログアウト完了"
    successMessage="ログアウトしました。"
    errorTitle="ログアウト失敗"
    errorMessage="ログアウトに失敗しました。もう一度お試しください。"
    onConfirm={handleConfirmAction}
    onCancel={handleCancelAction}
    onClose={handleConfirmClose}
/>