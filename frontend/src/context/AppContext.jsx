import { createContext, useEffect, useState } from "react";
import { AppConstants } from "../utils/constants";
import axios from "axios";
import { toast } from "react-toastify";

export const AppContext = createContext();

export const AppContextProvider = (props) => {
    const backendUrl = AppConstants.BACKEND_URL;
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [userData, setUserData] = useState(null)

    const getUserData = async () => {
        try {
            const response = await axios.get(backendUrl+"/user")
            if (response.status === 200){
                setUserData(response.data)
            } else {
                toast.error("Impossibile ottenere i dati del profilo")
            }
        } catch (err){
            toast.error(err.message)
        }
    }

    const getAuthState = async () => {
        try {
            axios.defaults.withCredentials = true
            const response = await axios.get(backendUrl+"/is-authenticated");
            if (response.status === 200 && response.data === true){
                setIsLoggedIn(true);
                await getUserData();
            } else {
                setIsLoggedIn(false);
            }
        } catch (err){
            console.error(err)
            setIsLoggedIn(false);
        }
    }

    useEffect(() => {
        getAuthState();
    }, []);

    const contextValue =  {
        backendUrl,
        isLoggedIn, setIsLoggedIn,
        userData, setUserData,
        getUserData, getAuthState
    };

    return (
        <AppContext.Provider value={contextValue}>
            {props.children}
        </AppContext.Provider>
    )
}