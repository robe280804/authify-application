import { useContext, useEffect, useRef, useState } from "react";
import { assets } from "../assets/assets"
import { useNavigate } from 'react-router-dom'
import { AppContext } from '../context/AppContext'
import axios from "axios";
import { toast } from "react-toastify";


export const MenuBar = () => {
    const navigate = useNavigate();
    const { userData, backendUrl, setIsLoggedIn, setUserData } = useContext(AppContext)
    const [dropDownOpen, setDropDownOpen] = useState(false);
    const dropDownRef = useRef(null);

    // Chiusura dropdown se clicco al di fuori di esso
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (dropDownRef.current && !dropDownRef.current.contains(event.target)){
                setDropDownOpen(false);
            }
        };

        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside)
    }, []);

    const handlleLogout = async () => {
        try {
            axios.defaults.withCredentials = true;
            const response = await axios.post(`${backendUrl}/logout`);
            if (response.status === 200){
                setIsLoggedIn(false);
                setUserData(null)
                navigate("/")
            }
        } catch (err){
            toast.error("Errore, sei stato reindirizzato al login", err.status)
            navigate("/login")
        }
    }

    const sendVerificationEmailOtp = async () => {
        try {
            axios.defaults.withCredentials = true;
            const response = await axios.post(`${backendUrl}/send-otp`)

            if (response.status === 200){
                toast.success("Ti è stata inviata un email con il codice OTP ")
                navigate("/email-verify")
            } else {
                toast.error("Qualcosa è andato storto ")
            }
        } catch (err){
            toast.error(err.message)
        }
    }

    return (
        <nav className="navbar bg-white px-5 py-4 d-flex justify-content-between align-items-center">
            <div className="d-flex align-items-center gap-2">
                <img src={assets.logo_home} alt="logo" width={32} height={32} />
                <span className="fw-bold fs-4 text-dark"> Authify </span>
            </div>

            {/*Se userdata è null mostro il login, altrimenti il dropDown con icona del profilo (prima lettera del nome maiuscola) */}
            {userData !==  null ? (
                <div className="position-relative" ref={dropDownRef}>
                    <div className="bg-dark text-white rounded-circle d-flex justify-content-center align-items-center" 
                        style={{width: "40px", height: "40px", cursor: "pointer", userSelect: "none"}}
                        onClick={() => setDropDownOpen((prev) => !prev)}
                    >
                        {userData.name[0].toUpperCase()}  
                    </div>
                    {dropDownOpen && (
                        <div className="position-absolute shadow bg-white rounded p-2" style={{top: "50px", right: 0, zIndex: 100}}>

                            {/*Se l'account è verificato mostro il logout, atrimenti la verifica dell'email */}
                            {!userData.isAccountVerified && (
                                <div className="dropdown-item py-1 px-2" style={{cursor: "pointer"}} onClick={sendVerificationEmailOtp}>
                                    Verifica la tua email
                                </div>
                            )}
                            <div className="dropdown-item py-1 px-2 text-danger" style={{cursor: "pointer"}} onClick={handlleLogout}>
                                Logout
                            </div>
                        </div>
                    )}
                </div>
            ): (
                <div className = "btn btn-outline-dark rounded-pill px-3" 
                onClick = { () => navigate("/login") }>
                Login
                <i className="bi bi-arrow-right ms-2"></i>
            </div >
            )}
            
        </nav >
    )
}