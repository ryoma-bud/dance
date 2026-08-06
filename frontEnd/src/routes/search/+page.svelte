<script lang="ts">
    import { page } from "$app/state";
    import { goto } from "$app/navigation";

    let activeTab = $state<"all" | "users">("users");
    let query = $derived(page.url.searchParams.get("q") || "");

    let userResults = $state<any[]>([]);
    let videoResults = $state<any[]>([]);   
    let isLoading = $state(false);

    $effect(() => {
        if (query) {
            fetchSearchResults();
        }
    });

    // 検索した結果を呼び出す
    async function fetchSearchResults() {
        isLoading = true;
        try {
            const token = localStorage.getItem("token");

            // User検索API (점 . 제거됨)
            const userRes = await fetch(`http://localhost:8080/api/users/search?q=${encodeURIComponent(query)}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            if (userRes.ok) {
                userResults = await userRes.json();
            }

            // 投稿の検索API
            const videoRes = await fetch(`http://localhost:8080/api/posts/search?q=${encodeURIComponent(query)}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            if (videoRes.ok) {
                videoResults = await videoRes.json();
            }
        } catch (error) {
            console.error(error);
        } finally {
            isLoading = false;
        }
    }

    async function handleToggleFollow(user: any, event: MouseEvent) {
        event.stopPropagation();    // Clickした際にProfileページに行くことを防止
        
        try {
            const token = localStorage.getItem("token");
            const res = await fetch(`http://localhost:8080/api/follows/${user.id}`, {
                method: "POST",
                headers: { Authorization: `Bearer ${token}` }
            });

            if (res.ok) {
                const isNowFollowing = await res.json();
                userResults = userResults.map(u => 
                    u.id === user.id ? { ...u, isFollowing: isNowFollowing } : u
                );
            }
        } catch (error) {
            console.error(error);
        }
    }
</script>

<div class="max-w-4xl mx-auto px-6 py-8">

    <!-- おすすめ・ユーザーTab -->
    <div class="flex border-b border-gray-200 mb-6 gap-8 text-sm font-bold text-gray-500">
        <button 
            onclick={() => activeTab = 'all'} 
            class="pb-3 transition {activeTab === 'all' ? 'text-gray-900 border-b-2 border-gray-900' : 'hover:text-gray-700'}"
        >
            おすすめ
        </button>
        <button 
            onclick={() => activeTab = 'users'} 
            class="pb-3 transition {activeTab === 'users' ? 'text-gray-900 border-b-2 border-gray-900' : 'hover:text-gray-700'}"
        >
            ユーザー
        </button>
    </div>   

    {#if isLoading}
        <div class="py-12 text-center text-gray-400 text-sm">検索中...</div>
    {:else}
        <!-- おすすめTabをクリック -->
        {#if activeTab === 'all'}
            <div class="flex flex-col gap-8">
                
                <!-- 関連UserPreiview（最大2-3名ほど簡単に露出） -->
                {#if userResults.length > 0}
                    <div>
                        <div class="flex justify-between items-center mb-3">
                            <h2 class="text-xs font-bold text-gray-500 uppercase tracking-wider">ユーザー</h2>
                            <button onclick={() => activeTab = 'users'} class="text-xs font-semibold text-emerald-600 hover:underline">
                                すべて見る &gt;
                            </button>
                        </div>
                        <div class="flex flex-col gap-2">
                            {#each userResults.slice(0, 3) as user (user.id)}
                                <div
                                    onclick={() => goto(`/profile?id=${user.id}`)}
                                    class="flex items-center justify-between p-3 rounded-2xl hover:bg-gray-50 transition cursor-pointer border border-gray-100"
                                >
                                    <div class="flex items-center gap-3">
                                        <img
                                            src={user.profileImageUrl || "/images/DefaultProfile.png"}
                                            alt={user.name}
                                            class="w-10 h-10 rounded-full object-cover"
                                        />
                                        <div>
                                            <h3 class="font-bold text-gray-900 text-xs">{user.name}</h3>
                                            <p class="text-[11px] text-gray-400">@{user.email?.split('@')[0]}</p>
                                        </div>
                                    </div>
                                    <button
                                        onclick={(e) => handleToggleFollow(user, e)}
                                        class="px-4 py-1.5 rounded-xl text-xs font-bold transition
                                        {user.isFollowing 
                                            ? 'bg-gray-100 text-gray-600 hover:bg-rose-50 hover:text-rose-600' 
                                            : 'bg-emerald-500 text-white hover:bg-emerald-600'}"
                                    >
                                        {user.isFollowing ? 'フォロー中' : 'フォロー'}
                                    </button>
                                </div>
                            {/each}    
                        </div>
                    </div>
                {/if}
                
                <!-- おすすめの領域 -->
                <div>
                    <h2 class="text-xs font-bold text-gray-500 uppercase tracking-wider mb-3">おすすめ</h2>
                
                    <!-- 検索したらおすすめの投稿が出てくるところ -->
                    <div class="grid grid-cols-3 gap-4">
                        <!-- 例のカード -->
                        <div class="group relative aspect-[9/16] bg-gray-100 rounded-2xl overflow-hidden cursor-pointer hover:shadow-md transition">
                            <div class="absolute inset-0 flex items-center justify-center text-gray-400 text-xs font-semibold">
                                Thumbnail 1
                            </div>
                            <div class="absolute bottom-0 inset-x-0 p-3 bg-gradient-to-t from-black/60 to-transparent text-white text-xs">
                                <p class="font-bold truncate">Dance Challenge Video</p>
                                <p class="text-[10px] opacity-80">@Dancer_A</p>
                            </div>
                        </div>

                        <div class="group relative aspect-[9/16] bg-gray-100 rounded-2xl overflow-hidden cursor-pointer hover:shadow-md transition">
                            <div class="absolute inset-0 flex items-center justify-center text-gray-400 text-xs font-semibold">
                                Thumbnail Video 2
                            </div>
                            <div class="absolute bottom-0 inset-x-0 p-3 bg-gradient-to-t from-black/60 to-transparent text-white text-xs">
                                <p class="font-bold truncate">Hiphop Session</p>
                                <p class="text-[10px] opacity-80">@Popper_B</p>
                            </div>
                        </div>
                    </div>
                </div>

            </div> <!-- 👈 activeTab === 'all' 내부의 최외각 div 닫기 -->

        <!-- ユーザーTabをClick -->
        {:else if activeTab === 'users'}
            <div class="flex flex-col gap-4">
                {#if userResults.length === 0}
                    <div class="py-12 text-center text-gray-400 text-sm">"{query}" に一致するユーザーがいません。</div>
                {:else}
                    {#each userResults as user (user.id)}
                        <div
                            onclick={() => goto(`/profile?id=${user.id}`)}
                            class="flex items-center justify-between p-3.5 rounded-2xl hover:bg-gray-50 transition cursor-pointer border border-gray-100"
                        >
                            <div class="flex items-center gap-4">
                                <img
                                    src={user.profileImageUrl || "/images/DefaultProfile.png"}
                                    alt={user.name}
                                    class="w-12 h-12 rounded-full object-cover"
                                />
                                <div>
                                    <h3 class="font-bold text-gray-900 text-sm">{user.name}</h3>
                                    <p class="text-xs text-gray-400">@{user.email?.split('@')[0]}</p>
                                    <p class="text-xs text-gray-500 mt-0.5">
                                        <span class="font-semibold text-gray-700">{user.followerCount || 0}</span> フォロワー
                                    </p>
                                </div>
                            </div>

                            <button
                                onclick={(e) => handleToggleFollow(user, e)}
                                class="px-5 py-2 rounded-xl text-xs font-bold transition
                                {user.isFollowing 
                                    ? 'bg-gray-100 text-gray-600 hover:bg-rose-50 hover:text-rose-600' 
                                    : 'bg-emerald-500 text-white hover:bg-emerald-600 shadow-sm'}"
                            >
                                {user.isFollowing ? 'フォロー中' : 'フォロー'}
                            </button>
                        </div>
                    {/each}
                {/if}        
            </div>
        {/if} <!-- 👈 activeTab 조건문 완전 종료 -->
    {/if}
</div>