import { createContext, useState } from "react";
import { AppConstants } from "../utils/constants";

export const AppContext = createContext();

export const AppContextProvider = (props) => {
    const backendUrl = AppConstants.BACKEND_URL;
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [userData, setUserData] = useState(false)

    const contextValue =  {
        backendUrl,
        isLoggedIn, setIsLoggedIn,
        userData, setUserData
    };

    return (
        <AppContext.Provider value={contextValue}>
            {props.children}
        </AppContext.Provider>
    )
}