// src/store.ts
import { writable } from "svelte/store";

// Backendの UserInfoDto 構造とマッチんがするInterface定義
export interface UserInfo {
    id: number;
    name: string;
    email: string;
    role: string;
}

// Storeの値がUserInfoのtypeだったり、Logoutの状態だったらNull
export const authStore = writable<UserInfo | null>(null);