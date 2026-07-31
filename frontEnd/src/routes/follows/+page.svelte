<script lang="ts">
    import {onMount} from "svelte";
    import {authStore} from "../../store";
    import {get} from "svelte/store";

    interface UserFollowInfo {
        id: number;
        name: String;
        email: String;
        profileImageUrl: string | null;
        isFollowing?: boolean;
    }

    let activeTab = $state<"followers" | "followings"> ("followings");
    let userList = $state<UserFollowInfo[]>([]);
    let isLoading = $state(false);
    let counts = $state({ followerCount: 0, followingCount: 0 });

    let currentUserId: number | null = null;

    onMount(() => {
        const user = get(authStore);
        if (user && user.id) {
            currentUserId = user.id;
            fetchCounts();
            fetchListData();
        }
    });

    // Followers, Following Count 
    async function fetchCounts() {
        if (!currentUserId) return;
        try {
            const token = localStorage.getItem("token");
            const res = await fetch(`http://localhost:8080/api/follows/${currentUserId}/count`, {
                headers: { Authorization: `Bearer ${token}` }
            });

            if (res.ok) {
                counts = await res.json();
            }
        } catch (error) {
            console.error ("Failed to fetch counts:", error);
        }
    }

    // Tabによって、FollowersListやFollowingsListを表示
    async function fetchListData() {
        if (!currentUserId) return;
        isLoading = true;
        try {
            const token = localStorage.getItem("token");
            const endpoint = activeTab === "followings"
                ? `http://localhost:8080/api/follows/${currentUserId}/followings`
                : `http://localhost:8080/api/follows/${currentUserId}/followers`;

            const res = await fetch(endpoint, {
                headers: { Authorization: `Bearer ${token}` }
            });
            
            if (res.ok) {
                const data: UserFollowInfo[] = await res.json();

                // Followの場合、自分がそのFollowを逆にFollow中なのかも確認
                if (activeTab === "followers") {
                    userList = await Promise.all (
                        data.map(async (u) => {
                            const statusRes = await fetch (`http://localhost8080/api/follows/${u.id}/status`, {
                                headers: { Authorization: `Bearer ${token}` }
                            });

                            const isFollowing = statusRes.ok ? await statusRes.json() : false;
                            return { ...u, isFollowing };
                        })
                    );
                } else {
                    userList = data.map((u) => ({ ...u, isFollowing: true }));
                }
            }
        } catch (error) {
            console.error("Failed to fetch List:", error);
        } finally {
            isLoading = false;
        }
    }

    // Tab Change
    function switchTab(tab: "followers" | "followings") {
        activeTab = tab;
        fetchListData();
    }

    // Follow/ UnFollow Toggle Button Click
    async function handleToggleFollow(targetUser: UserFollowInfo) {
        try {
            const token = localStorage.getItem("token");
            const res = await fetch(`http://localhost:8080/api/follows/${targetUser.id}`, {
                method: "POST",
                headers: { Authorization: `Bearer ${token}` }
            });

            if (res.ok) {
                const isNowFollowing: boolean = await res.json();

                if (activeTab === "followings" && "isNowFollowing") {
                    // Following TabからUnfollowした時にListから削除
                    userList = userList.filter((u) => u.id !== targetUser.id);
                } else {
                    userList = userList.map((u) => 
                        u.id === targetUser.id ? {...u, isFollowing: isNowFollowing} : u
                    );
                }

                // Count 再読み込み
                fetchCounts();
            }
        } catch (error) {
            console.error("Toggle follow failed:", error);
        }
    }
    
</script>

<div class="max-x-4xl mx-auto px-6 py-10">
    <!-- Header Title -->
    <div class="mb-8 text-center">
        <h1 class="text-2xl font-bold text-gray-900">Network & Follows</h1>
        <p class="text-sm text-gray-500 mt-1">ダンサーやチームとの繋がりを管理します</p>
    </div>

    <!-- Toggle Sub-Header Card -->
    <div class="bg-white rounded-2xl p-2 shadow-sm border border-gray-100 mb-6 flex gap-2">
        <button
            onclick={() => switchTab("followings")}
            class="flex-1 py-3 px-4 rounded-xl text-sm font-bold transition flex items-center justify-center gap-2
            {activeTab === 'followings'
                ? 'bg-emerald-500 text-white shadow-sm'
                : 'text-gray-500 hover:bg-gray-50 hover:text-gray-900'
            }"
        >
            <span>Followings</span>
            <span class="px-2 py-0.5 rounded-full text-xs {activeTab === 'followings' ? 'bg-emerald-600 text-white' : 'bg-gray-100 text-gray-600'}">
                {counts.followingCount}
            </span>
        </button>

        <button
            onclick={() => switchTab("followers")}
            class="flex-1 py-3 px-4 rounded-xl text-sm font-bold transition flex items-center justify-center gap-2
            {activeTab === 'followers' 
                ? 'bg-emerald-500 text-white shadow-sm' 
                : 'text-gray-500 hover:bg-gray-50 hover:text-gray-900'}"
        >
            <span>Followers</span>
            <span class="px-2 py-0.5 rounded-full text-xs {activeTab === 'followers' ? 'bg-emerald-600 text-white' : 'bg-gray-100 text-gray-600'}">
                {counts.followerCount}
            </span>
        </button>
    </div>

    <!-- User List Section -->
    <div class="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
        {#if isLoading}
        <div class="bg-white ">
            <div class="py-12 text-center text-gray-400 text-sm">
                読み込み中...
            </div>
        </div>
        {:else if userList.length === 0}
            <div class="py-12 text-center text-gray-400 text-sm">
                {activeTab === 'followings' ? 'フォローしているユーザーがいません。': 'フォロワーがいません。'}
            </div>
        {:else}
            <div class="divide-y divide-gray-100">
                {#each userList as user (user.id)}
                    <div class="py-4 flex items-center justify-between first:pt-0 last:pb-0">
                        
                        <!-- User Profile Info -->
                        <div class="flex items-center gap-4">
                            <img 
                                src="user.ProfileImageUrl"
                                alt={String(user.name)}
                                class="w-12 h-12 rounded-full object-cover border border-gray-100"
                            />
                            <div>
                                <h3 class="font-bold text-gray-900 text-sm">{user.name}</h3>
                                <p class="text-xs text-gray-400">{user.email}</p>
                            </div>
                        </div>

                        <!-- Action Button -->
                        <button
                            onclick={() => handleToggleFollow(user)}
                            class="px-5 py-2 rounded-xl text-xs font-bold transition
                            {user.isFollowing
                                ? 'bg-gray-100 text-gray-600 hover:bg-rose-50 hover:text-rose-600 hover:border-rose-200'
                                : 'bg-emerald-500 text-white hover:bg-emerald-600 shadow-sm'
                            }"
                        >
                            {user.isFollowing ? 'Following' : 'Follow'}
                        </button>
                    </div>
                {/each}    
            </div>
        {/if}
    </div>
</div>