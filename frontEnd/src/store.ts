// src/store.ts
import { writable } from "svelte/store";

// 백엔드의 UserInfoDto 구조와 매칭되는 인터페이스 정의
export interface UserInfo {
    id: number;
    name: string;
    email: string;
    role: string;
}

// 스토어 값이 UserInfo 객체이거나, 로그아웃 상태일 때는 null임을 명시
export const authStore = writable<UserInfo | null>(null);