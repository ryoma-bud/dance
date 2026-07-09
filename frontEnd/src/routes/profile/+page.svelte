<script lang="ts">
    import {goto} from "$app/navigation";

    type Video = {
        id: number;
        title: string;
        thumbnailUrl: string | null;
    }

    type Profile = {
        name: string;
        role: string;
        profileText: string | null;
        profileImageUrl: string | null;
        genre: string | null;
        birthDate: string | null;
        followingCount: number;
        followerCount: number;
        videos: Video[];
    };

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
</script>

<div class="w-full px-6 py-8">
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
</div>