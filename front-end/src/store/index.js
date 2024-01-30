import {defineStore} from "pinia";
import axios from "axios";

export const useStore = defineStore('general', {
        state: () => {
            return {
                user: {
                    id: -1,
                    username: '',
                    email: '',
                    role: '',
                    avatar: null,
                    registerTime: null
                },
                forum: {
                    types: []
                }
            }
        }, persist: true, getters: {
            avatarUrl() {
                if (this.user.avatar) {
                    // console.log("尝试获取头像："+`${axios.defaults.baseURL}/api/image/get?imageName=${this.user.avatar}`)
                    return `${axios.defaults.baseURL}/api/image/get?imageName=${this.user.avatar}`
                } else return `https://i0.imgs.ovh/2023/12/10/fzBHK.md.jpeg`
            }
        }, actions: {
            findTypeById(id) {
                // console.log("pinia.forum.types : " + this.forum.types)
                for (let type of this.forum.types) {
                    if (type.id === id) return type
                }
            }, avatarUserUrl(avatar) {
                if (avatar) {
                    // console.log("尝试获取头像："+`${axios.defaults.baseURL}/api/image/get?imageName=${this.user.avatar}`)
                    return `${axios.defaults.baseURL}/api/image/get?imageName=${avatar}`
                } else return `https://i0.imgs.ovh/2023/12/10/fzBHK.md.jpeg`
            }
        }
    }
)
