import { useRef, useState } from "react";
import { assets } from "../assets/assets"
import axios from "axios";
import { useContext } from "react";
import { AppContext } from "../context/AppContext";
import { Link, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";


export const ResetPassword = () => {
    const inputRef = useRef([]);
    const [loading, setLoading] = useState(false);
    const [email, setEmail] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const { getUserData, isLoggedIn, userData, backendUrl } = useContext(AppContext);
    const [isEmailSent, setIsEmailSent] = useState(false);
    const [otp, setOtp] = useState("");
    const [isOtpSubmitted, setIsOtpSubmit] = useState(false)
    const navigate = useNavigate();

    axios.defaults.withCredentials = true;

    const handleChange = (e, index) => {
        // Il client può inserire solo numeri
        const value = e.target.value.replace(/\D/, "");
        e.target.value = value;

        // Sposto il focus sul numeroi accanto
        if (value && index < 5) {
            inputRef.current[index + 1].focus();
        }
    }

    // Permetto di cancellare il numero precedente
    const handleKeyDown = (e, index) => {
        if (e.key === "Backspace" && !e.target.value && index > 0) {
            inputRef.current[index - 1].focus();
        }
    }

    // In caso di copia e incolla del codice, lo separa e compila in modo corretto
    const handlePaste = (e) => {
        e.preventDefault();
        const paste = e.clipboardData.getData("text").slice(0, 6).split("");
        paste.forEach((digit, index) => {
            if (inputRef.current[index]) {
                inputRef.current[index].value = digit
            }
        });
        const next = paste.length < 6 ? paste.lenght : 5;
        inputRef.current[next].focus();
    }


    const submitEmail = async (e) => {
        e.preventDefault();

        try {
            const response = await axios.post(backendUrl + "/send-reset-otp?email=" + email)
            if (response.status === 200) {
                toast.success("Ti è stata inviata un email con il codice OTP.");
                setIsEmailSent(true);
                setEmail(email);
            } else {
                toast.error("Qualcosa è andato storto, riprova.")
            }
        } catch (err) {
            toast.error(err.response.data.message)
        } finally {
            setLoading(false);
        }
    }

    const handleVerify = () => {
        const otp = inputRef.current.map(input => input.value).join("");
        if (otp.length !== 6) {
            toast.error("Inserisci tutti e 6 i numeri");
            return;
        }

        setOtp(otp);
        setIsOtpSubmit(true)
    }

    const submitNewPassword = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const response = await axios.post(backendUrl + "/reset-password", { otp, email, newPassword });

            if (response.status === 200) {
                toast.success("Password aggiornata con successo.")
                navigate("/login")
            } else {
                toast.error("Qualcosa è andato storto, riprova.")
            }
        } catch (err) {
            if (err.response && err.response.data) {
                const data = err.response.data;

                // Prendi solo i messaggi di errore dei campi 
                const fieldErrors = [data.name, data.email, data.password].filter(Boolean);

                if (fieldErrors.length > 0) {
                    fieldErrors.forEach(msg => toast.error(msg));
                } else if (data.message) {
                    toast.error(data.message);
                } else {
                    toast.error("Errore sconosciuto dal server");
                }
            } else {
                toast.error(err.message);
            }
        } finally {
            setLoading(false)
        }
    }

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

            {/* Reset password card */}
            {!isEmailSent && (
                <div className="rounded-4 p-5 text-center bg-white" style={{ width: "100%", maxWidth: "400px" }}>
                    <h4 className="mb-2"> Reset password </h4>
                    <p className="tmb-4"> Inserisci la tua email registrata </p>
                    <form onSubmit={submitEmail}>
                        <div className="input-group mb-4 bg-secondary bg-opacity-10 rounded-pill">
                            <span className="input-group-text bg-transapernt border-0 ps-4">
                                <i className="bi bi-envelope"></i>
                            </span>
                            <input
                                type="email"
                                className="email form-control bg-transparent border-0 ps-1 pe-4 rounded-end"
                                placeholder="Inserisci la tua email"
                                style={{ height: "50px" }}
                                onChange={(e) => setEmail(e.target.value)}
                                value={email}
                                required
                            />
                        </div>
                        <button className="btn btn-primary w-100 py-2" type="submit" disabled={loading}>
                            {loading ? "In corso" : "Invia"}
                        </button>
                    </form>
                </div>
            )}

            {/*OTP card */}
            {!isOtpSubmitted && isEmailSent && (
                <div className="p-5 rounded-4 shadow bg-white" style={{ width: "400px" }}>
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
            )}

            {/*New password form */}
            {isOtpSubmitted && isEmailSent && (
                <div className="rounded-4 p-4 text-center bg-white" style={{ width: "100%", maxWidth: "400px" }}>
                    <h4>Nuova password</h4>
                    <p className="mb-4">Inserisci la nuova password qui sotto</p>
                    <form onSubmit={submitNewPassword}>
                        <div className="input-group mb-4 bg-secondar bg-opacity-10 rounded-pill">
                            <span className="input-group-text bg-transparent border-0 p-4">
                                <i className="bi bi-person-fill-lock"></i>
                            </span>
                            <input
                                type="password"
                                className="form-control bg-transparent border-0 ps-1 pe-4 rounded-end"
                                placeholder="**************"
                                style={{ height: "50px" }}
                                onChange={(e) => setNewPassword(e.target.value)}
                                value={newPassword}
                                required
                            />
                        </div>
                        <button type="submit" className="btn btn-primary w-100" onClick={handleVerify} disabled={loading}>
                            {loading ? "In corso" : "Invia"}
                        </button>
                    </form>
                </div>

            )}
        </div>
    )

}