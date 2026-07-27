<script lang="ts">
    import { goto } from "$app/navigation";
    import { onMount } from "svelte";
 
    const API_BASE_URL = "http://localhost:8080";
    const USER_ID = 1;
    const DEFAULT_ICON_URL = "/images/ProfileIcon.png";
 
    type Video = {
        id: number;
        title: string;
        thumbnailUrl: string | null;
    };
 
    type Profile = {
        name: string;
        role: string;
        profileText: string | null;
        profileImageUrl: string | null;
        genres: string[];
        birthDate: string | null;
        followingCount: number;
        followerCount: number;
        videos: Video[];
    };
 
    type ProfileResponse = {
        id?: number;
        name?: string;
        username?: string;
        role?: string;
        profileText?: string | null;
        profile_text?: string | null;
        profileImageUrl?: string | null;
        profile_image_url?: string | null;
        genres?: string[] | string | null;
        genre?: string | null;
        birthDate?: string | null;
        birth_date?: string | null;
        followingCount?: number | null;
        following_count?: number | null;
        followerCount?: number | null;
        follower_count?: number | null;
        videos?: Video[] | null;
    };
 
    let profile = $state<Profile>({
        name: "",
        role: "USER",
        profileText: null,
        profileImageUrl: null,
        genres: [],
        birthDate: null,
        followingCount: 0,
        followerCount: 0,
        videos: [],
    });
 
    let isLoading = $state(false);
    let errorMessage = $state("");
    let shouldShowDefaultIcon = $state(false);
 
    const displayProfileImageUrl = $derived(
        shouldShowDefaultIcon
            ? DEFAULT_ICON_URL
            : profile.profileImageUrl || DEFAULT_ICON_URL,
    );
 
    const displayGenres = $derived(
        profile.genres.length > 0 ? profile.genres.join(" / ") : "未設定",
    );
 
    function normalizeImageUrl(value: string | null | undefined): string | null {
        if (!value || !value.trim()) {
            return null;
        }
 
        const imageUrl = value.trim();
 
        if (imageUrl === "null" || imageUrl === "undefined") {
            return null;
        }
 
        if (
            imageUrl.startsWith("http://") ||
            imageUrl.startsWith("https://") ||
            imageUrl.startsWith("data:") ||
            imageUrl.startsWith("blob:")
        ) {
            return imageUrl;
        }
 
        if (imageUrl.startsWith("/uploads/")) {
            return `${API_BASE_URL}${imageUrl}`;
        }
 
        if (imageUrl.startsWith("uploads/")) {
            return `${API_BASE_URL}/${imageUrl}`;
        }
 
        if (imageUrl.startsWith("/")) {
            return imageUrl;
        }
 
        return `/${imageUrl}`;
    }
 
    function normalizeGenres(value: string[] | string | null | undefined): string[] {
        if (!value) {
            return [];
        }
 
        if (Array.isArray(value)) {
            return value
                .map((genre) => genre.trim())
                .filter((genre) => genre.length > 0);
        }
 
        return value
            .split(",")
            .map((genre) => genre.trim())
            .filter((genre) => genre.length > 0);
    }
 
    function formatBirthDate(value: string | null | undefined): string | null {
        if (!value) {
            return null;
        }
 
        return value.slice(0, 10);
    }
 
    function handleProfileImageError(event: Event) {
        const img = event.currentTarget as HTMLImageElement;
 
        if (img.src.endsWith(DEFAULT_ICON_URL)) {
            return;
        }
 
        shouldShowDefaultIcon = true;
        img.src = DEFAULT_ICON_URL;
    }
 
    function applyProfile(data: ProfileResponse) {
        profile.name = data.name ?? data.username ?? "DanStar_User";
        profile.role = data.role ?? "USER";
        profile.profileText = data.profileText ?? data.profile_text ?? null;
        profile.profileImageUrl = normalizeImageUrl(
            data.profileImageUrl ?? data.profile_image_url,
        );
        profile.genres = normalizeGenres(data.genres ?? data.genre ?? null);
        profile.birthDate = formatBirthDate(data.birthDate ?? data.birth_date);
        profile.followingCount = data.followingCount ?? data.following_count ?? 0;
        profile.followerCount = data.followerCount ?? data.follower_count ?? 0;
        profile.videos = data.videos ?? [];
        shouldShowDefaultIcon = false;
    }
 
    async function loadProfile() {
        isLoading = true;
        errorMessage = "";
 
        try {
            const response = await fetch(`${API_BASE_URL}/api/users/${USER_ID}/profile`);
 
            if (!response.ok) {
                throw new Error(`プロフィール取得に失敗しました。status=${response.status}`);
            }
 
            const data: ProfileResponse = await response.json();
            applyProfile(data);
        } catch (error) {
            console.error(error);
            errorMessage = "プロフィール情報を取得できませんでした。APIの起動状態やURLを確認してください。";
            profile.profileImageUrl = null;
            profile.genres = [];
            shouldShowDefaultIcon = true;
        } finally {
            isLoading = false;
        }
    }
 
    onMount(() => {
        loadProfile();
    });
</script>
 
<div class="w-full px-6 py-8">
    <div class="mx-auto max-w-6xl">
        {#if isLoading}
            <div class="mb-4 rounded-xl bg-blue-50 px-4 py-3 text-sm font-semibold text-blue-600">
                プロフィール情報を読み込み中です。
            </div>
        {/if}
 
        {#if errorMessage}
            <div class="mb-4 rounded-xl bg-red-50 px-4 py-3 text-sm font-semibold text-red-600">
                {errorMessage}
            </div>
        {/if}
 
        <section class="border-b border-gray-200 pb-8">
            <div class="flex flex-col gap-6 sm:flex-row sm:items-start sm:gap-8">
                <img
                    src={displayProfileImageUrl}
                    alt="Profile"
                    class="h-24 w-24 rounded-full border border-gray-200 object-cover"
                    onerror={handleProfileImageError}
                />
 
                <div class="flex min-w-0 flex-1 flex-col gap-4">
                    <div class="flex flex-wrap items-center gap-3">
                        <h1 class="truncate text-2xl font-bold text-gray-900">
                            {profile.name}
                        </h1>
 
                        <span class="rounded-full bg-emerald-50 px-2 py-1 text-xs font-semibold text-emerald-600">
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
                        <div class="rounded-full bg-gray-100 px-3 py-1">
                            ジャンル：{displayGenres}
                        </div>
 
                        <div class="rounded-full bg-gray-100 px-3 py-1">
                            生年月日：{profile.birthDate ?? "未設定"}
                        </div>
                    </div>
 
                    <div class="flex flex-wrap items-center gap-3">
                        <button
                            type="button"
                            onclick={() => goto("/profile/edit")}
                            class="rounded-xl bg-gray-100 px-5 py-2 text-sm font-bold text-gray-900 transition hover:bg-gray-200"
                        >
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
                    class="border-b-2 border-gray-900 px-8 py-3 text-sm font-bold text-gray-900"
                >
                    投稿
                </button>
            </div>
 
            {#if profile.videos.length > 0}
                <div class="grid grid-cols-2 gap-4 py-6 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5">
                    {#each profile.videos as video}
                        <button
                            type="button"
                            class="group overflow-hidden rounded-xl bg-gray-100 text-left transition hover:opacity-90"
                        >
                            <div class="relative aspect-[9/16] w-full bg-gray-200">
                                {#if video.thumbnailUrl}
                                    <img
                                        src={video.thumbnailUrl}
                                        alt={video.title}
                                        class="h-full w-full object-cover"
                                    />
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