import axios from "axios";
import {ElMessage} from "element-plus";

const authItemName = "access_token"
const defaultFailure = (message, code, url) => {
    console.warn(`请求地址:${url},code:${code},信息:${message}`);
    ElMessage.warning(`error:${message}`);
}
const defaultError = (err) => {
    console.warn(`error:${err}`);
    ElMessage.warning(`发生了错误，错误信息：${err}`);
}

function takeAccessToken() {
    const str = localStorage.getItem(authItemName) || sessionStorage.getItem(authItemName)
    if (!str){
        return null;
    }
    const authObj = JSON.parse(str);
    if (authObj.expireTime < Date.now()) {
        localStorage.removeItem(authItemName)
        sessionStorage.removeItem(authItemName)
        ElMessage.info("token过期，请重新登录")
        return null
    }
    return authObj.token;
}
function storeAccessToken(token, remember, expireTime) {
    const authObj = {
        token: token,
        expireTime:expireTime
    }
    const str = JSON.stringify(authObj);
    if (remember) {
        localStorage.setItem(authItemName, str)
    }else {
        sessionStorage.setItem(authItemName, str)
    }
}
function accessHeader(){
    const token = takeAccessToken();
    const exitToken = token ? {
        'Authorization': `Bearer`+` `+token
    } : {};
    // ElMessage.info(JSON.stringify (exitToken));
    return exitToken;

}
function internalPost(url, data, header, success, failure, error = defaultError) {
    axios.post(url, data, {headers: header}).then(({data}) => {
        if (data.code === 200) {
            success(data.data)
        } else {
            failure(data.message, data.code, url)
        }
    }).catch(err => error(err))
}

function internalGet(url, header, success, failure, error = defaultError) {
    axios.get(url, {headers: header}).then(({data}) => {
        if (data.code === 200) {
            success(data.data)
        } else {
            failure(data.message, data.code, url)
        }
    }).catch(err => error(err))
}

function get(url, success, failure = defaultFailure){
    internalGet(url,accessHeader(),success,failure)
}
function post(url,data,success,failure = defaultFailure){
    internalPost(url,data,accessHeader(),success,failure)
}

function login(username, password, remember, success, failure = defaultFailure) {
    internalPost("/api/auth/login", {
            username: username,
            password: password,
        }, {
            'Content-Type': 'application/x-www-form-urlencoded',
        },(data) => {
            storeAccessToken(data.token, remember, data.expireTime)
            ElMessage.success(`登录成功,欢迎${data.username}使用！`)
            success(data)
        },failure)
}

function deleteAccessToken() {
    localStorage.removeItem(authItemName)
    sessionStorage.removeItem(authItemName)
}

function logout(success, failure = defaultFailure){
    get('api/auth/logout',()=>{
        deleteAccessToken();
        ElMessage.success("登出成功,欢迎下次使用!")
        success()
    },failure)
}
function unauthorized(){
    return !takeAccessToken()
}
export {login,logout,get,post,unauthorized}