import { createContext, useState } from "react";
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

    const contextValue =  {
        backendUrl,
        isLoggedIn, setIsLoggedIn,
        userData, setUserData,
        getUserData
    };

    return (
        <AppContext.Provider value={contextValue}>
            {props.children}
        </AppContext.Provider>
    )
}