import { Link, useNavigate } from "react-router-dom"
import { assets } from "../assets/assets"
import { useContext, useEffect, useRef, useState } from "react"
import { AppContext } from "../context/AppContext";
import { toast } from "react-toastify";
import axios from "axios";

export const EmailVerify = () => {
    const inputRef = useRef([]);
    const [loading, setLoading] = useState(false);
    const {getUserData, isLoggedIn, userData, backendUrl} = useContext(AppContext);
    const navigate = useNavigate();

    const handleChange = (e, index) => {
        // Il client può inserire solo numeri
        const value = e.target.value.replace(/\D/, "");
        e.target.value = value;

        // Sposto il focus sul numeroi accanto
        if (value && index <  5){
            inputRef.current[index + 1].focus();
        } 
    }

    // Permetto di cancellare il numero precedente
    const handleKeyDown = (e, index) => {
        if (e.key === "Backspace" && !e.target.value && index > 0){
            inputRef.current[index -1].focus();
        }
    }

    // In caso di copia e incolla del codice, lo separa e compila in modo corretto
    const handlePaste = (e) => {
        e.preventDefault();
        const paste = e.clipboardData.getData("text").slice(0, 6).split("");
        paste.forEach((digit, index) => {
            if (inputRef.current[index]){
                inputRef.current[index].value = digit
            }
        });
        const next = paste.length < 6 ? paste.lenght : 5;
        inputRef.current[next].focus();
    }

    const handleVerify = async () => {
        const otp = inputRef.current.map(input => input.value).join("");
        if (otp.length !== 6){
            toast.error("Inserisci tutti e 6 i numeri");
            return;
        }

        setLoading(true)
        try {
            axios.defaults.withCredentials = true;
            const response = await axios.post(`${backendUrl}/verify-otp`, {otp});
            if (response.status === 200){
                toast.success("OTP verificato con successo!")
                getUserData();
                navigate("/")
            } else {
                toast.error("OTP invalido")
            }
        } catch (err){
            toast.error(err.response.data.message)
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        isLoggedIn && userData.isAccountVerify && navigate("/");
    }, [isLoggedIn, userData]);

    return (
        <div className="email-verify-container d-flex align-items-center justify-content-center vh-100 position-relative"
            style={{ background: "linear-gradient(90deg, #6a5af9, #8268f9)", borderRadius: "none" }}
        >
            <Link to="/" className="position-absolute top-0 start-0 p-4 d-flex align-items-center gap-2 text-decoration-none">
                <img src={assets.logo_home} alt="logo" height={32} width={32} />
                <span className="fs-4 fw-semibold text-light">
                    Authify
                </span>
            </Link>

            <div className="p-5 rounded-4 shadow bg-white" style={{width: "400px"}}>
                <h4 className="text-center fw-bold mb-2 "> Verifica l'email con OTP </h4>
                <p className="text-center mb-4">
                    Inserisci il codice a 6 cifre inviato alla tua email
                </p>
                <div className="d-flex justify-content-between gap-2 mb-4 text-center text-white-50 mb-2">
                    {[...Array(6)].map((_, i) => (
                        <input 
                            key={i}
                            type="text" 
                            maxLength={1}
                            className="form-control text-center fs-4 otp-input"  
                            ref={(el) => inputRef.current[i] = el}
                            onChange={(e) => handleChange(e, i)}
                            onKeyDown={(e) => handleKeyDown(e, i)}
                            onPaste={handlePaste}
                        /> 
                    ))}
                </div>
                <button className="btn btn-primary w-100 fw-semibold" disabled={loading} onClick={handleVerify}>
                    {loading ? "verifica in corso..." : "Verifica"}
                </button>
            </div>
        </div>
    )
}
