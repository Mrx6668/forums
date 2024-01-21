import {defineStore} from "pinia";
import axios from "axios";

export const useStore = defineStore('general', {
    state: () => {
        return {
            user: {
                username: '',
                email: '',
                role: '',
                avatar: null,
                registerTime: null
            }
        }
    }, getters: {
        avatarUrl() {
            if (this.user.avatar){
                // console.log("尝试获取头像："+`${axios.defaults.baseURL}/api/image/get?imageName=${this.user.avatar}`)
                return `${axios.defaults.baseURL}/api/image/get?imageName=${this.user.avatar}`
            }
            else return `https://i0.imgs.ovh/2023/12/10/fzBHK.md.jpeg`
        }
    }
})
