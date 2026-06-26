<script>
    import { onMount } from "svelte";
    import { goto } from "$app/navigation";
    import { authStore } from "../../store";

    /** @type {any} **/
    let userInfo = $state(null);

    // storeが変わったら userInfo に反映
    authStore.subscribe(value => {
        userInfo = value;
    });

    onMount(() => {
        // 再読み込みをすればstoreが初期化されるので、localStroeから読み込む
        if (!userInfo) {
            const storedUserInfo = localStorage.getItem('userInfo');
            if (storedUserInfo) {
                authStore.set(JSON.parse(storedUserInfo));
            } else {
                alert('ログインが必要です。');
                goto('/');
            }
        }
    });

    function handleLogout() {
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        authStore.set(null);
        goto("/");  // 全ての情報を消して、ログイン画面に
    }

</script>

<svelte:head>
    <title>DanStar - Main</title>
</svelte:head>

{#if userInfo}
    <div class="flex min-h-screen flex-col items-center justify-center bg-[#08120e] p-6 text-white">
        <div class="w-full max-w-[400px] rounded-3xl border border-lime-400/20 bg-black/40 p-8 shadow-[0_20px_60px_rgba(0,0,0,0.5)]">
            <h2 class="mb-6 text-center text-2xl font-bold text-lime-300">DanStar Main</h2>

            <div class="space-y-4 rounded-xl bg-white/5 p-6 text-sm text-green-50">
                <p><span class="font-bold text-lime-400/70">ID:</span> {userInfo.id}</p>
                <p><span class="font-bold text-lime-400/70">名前:</span> {userInfo.name}</p>
                <p><span class="font-bold text-lime-400/70">Email:</span> {userInfo.email}</p>
                <p><span class="font-bold text-lime-400/70">Role:</span> {userInfo.role}</p>
            </div>

            <button
                type="button"
                onclick={handleLogout}
                class="mt-8 w-full rounded-2xl border border-lime-400/30 bg-transparent px-4 py-3 text-base font-bold text-lime-300 transition hover:border-lime-300/50 hover:text-lime-200"
            >
                ログアウト
            </button>
        </div>
    </div>
{:else}
    <div class="flex min-h-screen items-center justify-center bg-[#08120e] text-lime-300">
        ローディング中...
    </div>

{/if}