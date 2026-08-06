<script lang="ts">
    import {goto} from "$app/navigation";
    import { page } from "$app/state";

    type Video = {
        id: number;
        title: string;
        thumbnailUrl: string | null;
    }

    type Profile = {
        id?: number
        name: string;
        role: string;
        profileText: string | null;
        profileImageUrl: string | null;
        genre: string | null;
        birthDate: string | null;
        followingCount: number;
        followerCount: number;
        videos: Video[];
        isFollowing?: boolean;      // 他の人のProfileに入った際のFollow状態
    };

    // QueryStringからTargetId持ってくる
    let targetUserId = $derived(page.url.searchParams.get("id"));


    // 任意設定
    let profile = $state<Profile>({
        name: "DanStar_User",
        role: "USER",
        profileText: null,
        profileImageUrl: null,
        genre: "HIPHOP",
        birthDate: "2000-01-01",
        followingCount: 95,
        followerCount: 14,
        videos: [
            {
                id: 1,
                title: "video",
                thumbnailUrl: null,
            },
            {
                id: 2,
                title: "video",
                thumbnailUrl: null,
            }
        ]
    });

    let isMyProfile = $state(true);

    // targetUserIdが変わったり、ページがロードする時に実行
    $effect(() => {
        loadProfileData();
    })

    async function loadProfileData() {
        const token = localStorage.getItem("token");
        const storedUser = localStorage.getItem("userInfo");
        const myInfo = storedUser ? JSON.parse(storedUser) : null;

        // targetUserIdがないかログインしているIDと一緒だったら、自分のProfile
        if (!targetUserId || (myInfo && String(myInfo.id) === String(targetUserId))) {
            isMyProfile = true;
            fetchMyProfile(token);
        } else {
            isMyProfile = false;
            fetchOtherProfile(targetUserId, token);
        }
    }

    // 自分のProfile照会
    async function fetchMyProfile(token: string | null) {
        try {
            const res = await fetch("http://localhost:8080/api/users/me/profile", {
                headers: { Authorization: `Bearer ${token}` }
            });
            if (res.ok) {
                profile = await res.json();
            }
        } catch (err) {
            console.error(err);
        }
    }

    // 他の人のProfile照会
    async function fetchOtherProfile(userId: string, token: string | null) {
        try {
            const res = await fetch(`http://localhost:8080/api/users/${userId}/profile`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            if (res.ok) {
                profile = await res.json();
            }
        } catch (error) {
            console.error(error);
        }
    }

    // FollowやUnfollow toggle button click(他人のProfileの場合)
    async function handleToggleFollow() {
        if (!targetUserId) return;
        try {
            const token = localStorage.getItem("token");
            const res = await fetch(`http://localhost:8080/api/follows/${targetUserId}`, {
                method: "POST",
                headers: { Authorization: `Bearer ${token}` }
            });

            if (res.ok) {
                const isNowFollowing = await res.json();
                profile.isFollowing = isNowFollowing;

                // Follower数を更新
                profile.followerCount += isNowFollowing ? 1 : -1;
            }
        } catch (error) {
            console.error(error);
        }
    }
</script>



<div class="w-full px-6 py-8">
    <div>
        <!-- 프로필 헤더 영역 -->
        <section class="border-b border-gray-200 pb-8">
            <div class="flex items-start gap-8">
                <img
                    src={profile.profileImageUrl ?? "/images/ProfileIcon.png"}
                    alt="Profile"
                    class="w-24 h-24 rounded-full object-cover border border-gray-200"
                />
                <div class="flex min-w-0 flex-1 flex-col gap-4">
                    <div class="flex flex-wrap items-center gap-3">
                        <h1 class="truncate text-2xl font-bold text-gray-900">
                            {profile.name}
                        </h1>
                        <span class="text-xs px-2 py-1 rounded-full bg-emerald-50 text-emerald-600 font-semibold">
                            {profile.role}
                        </span>
                    </div>

                    <div class="flex flex-wrap items-center gap-6 text-sm text-gray-900">
                        <p>
                            <span class="font-bold">{profile.followingCount}</span>
                            <span class="ml-1">フォロー中</span>
                        </p>
                        <p>
                            <span class="font-bold">{profile.followerCount}</span>
                            <span class="ml-1">フォロワー</span>
                        </p>
                    </div>

                    <div class="flex flex-wrap items-center gap-3 text-sm text-gray-700">
                        <div class="rounded-full bg-gray-100 px-3 py-1 text-xs">
                            ジャンル：{profile.genre ?? "未設定"}
                        </div>
                        <div class="rounded-full bg-gray-100 px-3 py-1 text-xs">
                            生年月日：{profile.birthDate ?? "未設定"}
                        </div>
                    </div>

                    <!-- 내 프로필 / 타인 프로필 버튼 분기 -->
                    <div class="flex flex-wrap items-center gap-3">
                        {#if isMyProfile}
                            <button
                                type="button"
                                onclick={() => goto("/profile/edit")}
                                class="rounded-xl bg-gray-100 px-5 py-2 text-sm font-bold text-gray-900 transition hover:bg-gray-200">
                                プロフィールを編集する
                            </button>
                        {:else}
                            <button
                                type="button"
                                onclick={handleToggleFollow}
                                class="rounded-xl px-6 py-2 text-xs font-bold transition
                                {profile.isFollowing 
                                    ? 'bg-gray-100 text-gray-600 hover:bg-rose-50 hover:text-rose-600' 
                                    : 'bg-emerald-500 text-white hover:bg-emerald-600 shadow-sm'}">
                                {profile.isFollowing ? 'フォロー中' : 'フォロー'}
                            </button>
                        {/if}
                    </div>

                    <p class="max-w-2xl whitespace-pre-wrap text-sm text-gray-900">
                        {profile.profileText ?? "自己紹介はまだ記入されていません。"}
                    </p>
                </div>
            </div>
        </section>

        <!-- 게시물(Video) 영역 -->
        <section class="pt-6">
            <div class="flex border-b border-gray-200">
                <button
                    type="button"
                    class="border-b-2 border-gray-900 px-8 py-3 text-sm font-bold text-gray-900">
                    投稿
                </button>    
            </div>

            {#if profile.videos && profile.videos.length > 0}
                <!-- 원래 코드와 동일한 grid 속성 적용 -->
                <div class="grid grid-cols-2 gap-4 py-6 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5">
                    {#each profile.videos as video (video.id)}
                        <button
                            type="button"
                            class="group overflow-hidden rounded-xl bg-gray-100 text-left transition hover:opacity-90">
                            <div class="relative aspect-[9/16] w-full bg-gray-200">
                                {#if video.thumbnailUrl}
                                    <img
                                        src={video.thumbnailUrl}
                                        alt={video.title}
                                        class="h-full w-full object-cover"/>
                                {:else}
                                    <div class="flex h-full w-full items-center justify-center bg-gray-200 text-sm font-bold text-gray-400">
                                        No Video
                                    </div>
                                {/if}
                            </div>
                            <div class="p-2">
                                <p class="truncate text-sm font-semibold text-gray-900">
                                    {video.title}
                                </p>
                            </div>
                        </button>
                    {/each}
                </div>
            {:else}
                <div class="flex min-h-64 items-center justify-center text-sm text-gray-400">
                    投稿した動画はまだありません。
                </div>
            {/if}
        </section>
    </div>
</div>



<!-- <div class="w-full px-6 py-8">
    <div class="mx-auto max-6xl">
        <section class="border-b border-gray-200 pb-8">
            <div class="flex items-start gap-8">
                <img
                    src={profile.profileImageUrl ?? "/images/ProfileIcon.png"}
                    alt="Profile"
                    class="w-24 h24 rounded-full object-cover border border-gray-200"
                />
                <div class="flex min-w-0 flex-1 flex-col gap-4">
                    <div class="flex flex-wrap items-center gap-3">
                        <h1 class="transcate text-2xl font-bold text-gray-900">
                            {profile.name}
                        </h1>
                        <span class="text-xs px-2 py-1 rounded-full bg-emerald-50 text-emerald-600 font-semibold">
                            {profile.role}
                        </span>
                    </div>

                    <div class="flex flex-wrap items-center gap-6 text-sm text-gray-900">
                        <p>
                            <span class="font-bold">{profile.followingCount}</span>
                            <span class="ml-1">フォロー中</span>
                        </p>
                        <p>
                            <span class="font-bold">{profile.followerCount}</span>
                            <span class="ml-1">フォロー中</span>
                        </p>
                    </div>

                    <div class="flex flex-wrap items-center gap-3 text-sm text-gray-700">
                        <div class="rounded-full bg-gray-100 px-3 py-1">
                            ジャンル：{profile.genre ?? "未設定"}
                        </div>

                        <div class="rounded-full bg-gray-100 px-3 py-1">
                            生年月日：{profile.birthDate ?? "未設定"}
                        </div>
                    </div>

                    <div class="flex flex-wrap items-center gap-3">
                        <button
                            type="button"
                            onclick={() => goto("/profile/edit")}
                            class="rounded-xl bg-gray-100 px-5 py-2 text-sm font-bold text-gray-900 transition hover:bg-gray-200">
                            プロフィールを編集する
                        </button>
                    </div>

                    <p class="max-w-2xl whitespace-pre-wrap text-sm text-gray-900">
                        {profile.profileText ?? "自己紹介はまだ記入されていません。"}
                    </p>
                </div>
            </div>
        </section>

        <section class="pt-6">
            <div class="flex border-b border-gray-200">
                <button
                    type="button"
                    class="border-b-2 border-gray-900 px-8 py-3 text-sm font-bold text-gray-900">
                    投稿
                </button>    
            </div>

            {#if profile.videos.length > 0}
                <div class="grid grid-cols-2 gap-4 py-6 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5">
                    {#each profile.videos as video}
                        <button
                            type="button"
                            class="group overflow-hidden rounded-xl bg-gray-100 text-left transition hover:opacity-90">

                              <div class="relative aspect-[9/16] w-full bg-gray-200">
                                {#if video.thumbnailUrl}
                                    <img
                                        src={video.thumbnailUrl}
                                        alt={video.title}
                                        class="h-full w-ffull object-cover"/>
                                {:else}
                                    <div class="flex h-full w-full items-center justify-center bg-gray-200 text-sm font-bold text-gray-400">
                                        No Video
                                    </div>
                                {/if}
                            </div>

                            <div class="p-2">
                                <p class="truncate text-sm font-semibold text-gray-900">
                                    {video.title}
                                </p>
                            </div>
                        </button>
                    {/each}
                </div>
            {:else}
                <div class="flex min-h-64 items-center justify-center text-sm text-gray-400">
                    投稿した動画はまだありません。
                </div>
            {/if}
        </section>

        <hr class="border-gray-100" />
    </div>
</div> -->