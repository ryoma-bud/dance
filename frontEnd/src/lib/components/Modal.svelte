<script lang="ts">
    type ModalType = "alert" | "confirm";
    type ModalStatus = "default" | "loading" | "success" | "error";

    type Props = {
        isOpen: boolean;
        type?: ModalType;
        status?: ModalStatus;
        title?: string;
        message?: string;
        confirmText?: string;
        cancelText?: string;
        loadingText?: string;
        successTitle?: string;
        successMessage?: string;
        errorTitle?: string;
        errorMessage?: string;
        onConfirm?: () => void | Promise<void>;
        onCancel?: () => void;
        onClose?: () => void;
    };

    let {
        isOpen = $bindable(false),
        type = "alert",
        status = "default",
        title = "",
        message = "",
        confirmText = "OK",
        cancelText = "キャンセル",
        loadingText = "処理中です...",
        successTitle = "完了",
        successMessage = "処理が完了しました。",
        errorTitle = "エラー",
        errorMessage = "処理に失敗しました。",
        onConfirm,
        onCancel,
        onClose,
    }: Props = $props();

    const isLoading = $derived(status === "loading");

    function closeModal() {
        if (isLoading) {
            return;
        }

        isOpen = false;
        onClose?.();
    }

    function handleBackdropClick() {
        closeModal();
    }

    function stopPropagation(event: MouseEvent) {
        event.stopPropagation();
    }

    function handleCancel() {
        if (isLoading) {
            return;
        }

        onCancel?.();
        closeModal();
    }

    async function handleConfirm() {
        if (isLoading) {
            return;
        }

        await onConfirm?.();

        if (type === "alert" && status === "default") {
            closeModal();
        }
    }
</script>

{#if isOpen}
    <div
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 px-4"
        role="presentation"
        onclick={handleBackdropClick}
    >
        <div
            class="w-full max-w-sm rounded-2xl bg-white p-6 shadow-xl"
            role="dialog"
            aria-modal="true"
            onclick={stopPropagation}
        >
            {#if status === "loading"}
                <div class="flex flex-col items-center text-center">
                    <div class="h-12 w-12 animate-spin rounded-full border-4 border-gray-200 border-t-emerald-500"></div>

                    <h2 class="mt-5 text-lg font-bold text-gray-900">
                        処理中
                    </h2>

                    <p class="mt-2 text-sm leading-6 text-gray-500">
                        {loadingText}
                    </p>
                </div>
            {:else if status === "success"}
                <div class="flex flex-col items-center text-center">
                    <div class="flex h-14 w-14 items-center justify-center rounded-full bg-emerald-100">
                        <span class="text-2xl font-bold text-emerald-600">✓</span>
                    </div>

                    <h2 class="mt-5 text-lg font-bold text-gray-900">
                        {successTitle}
                    </h2>

                    <p class="mt-2 text-sm leading-6 text-gray-500">
                        {successMessage}
                    </p>

                    <button
                        type="button"
                        class="mt-6 w-full rounded-xl bg-emerald-500 px-4 py-3 text-sm font-bold text-white transition hover:bg-emerald-600"
                        onclick={closeModal}
                    >
                        {confirmText}
                    </button>
                </div>
            {:else if status === "error"}
                <div class="flex flex-col items-center text-center">
                    <div class="flex h-14 w-14 items-center justify-center rounded-full bg-red-100">
                        <span class="text-2xl font-bold text-red-600">!</span>
                    </div>

                    <h2 class="mt-5 text-lg font-bold text-gray-900">
                        {errorTitle}
                    </h2>

                    <p class="mt-2 text-sm leading-6 text-gray-500">
                        {errorMessage}
                    </p>

                    <button
                        type="button"
                        class="mt-6 w-full rounded-xl bg-red-500 px-4 py-3 text-sm font-bold text-white transition hover:bg-red-600"
                        onclick={closeModal}
                    >
                        閉じる
                    </button>
                </div>
            {:else}
                <div class="text-center">
                    <h2 class="text-lg font-bold text-gray-900">
                        {title}
                    </h2>

                    <p class="mt-3 text-sm leading-6 text-gray-500">
                        {message}
                    </p>
                </div>

                <div class="mt-6 flex gap-3">
                    {#if type === "confirm"}
                        <button
                            type="button"
                            class="w-full rounded-xl bg-gray-100 px-4 py-3 text-sm font-bold text-gray-700 transition hover:bg-gray-200"
                            onclick={handleCancel}
                        >
                            {cancelText}
                        </button>
                    {/if}

                    <button
                        type="button"
                        class="w-full rounded-xl bg-emerald-500 px-4 py-3 text-sm font-bold text-white transition hover:bg-emerald-600"
                        onclick={handleConfirm}
                    >
                        {confirmText}
                    </button>
                </div>
            {/if}
        </div>
    </div>
{/if}