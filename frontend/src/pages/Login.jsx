import { Link } from "react-router-dom"
import { assets } from "../assets/assets"
import { useContext, useState } from "react"
import axios from 'axios'
import { AppContext } from "../context/AppContext"
import { useNavigate } from "react-router-dom"
import { toast } from 'react-toastify'


export const Login = () => {
    const [isCreateAccount, setIsCreateAccount] = useState(false);
    const [name, setName] = useState("")
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")
    const [loading, setLoading] = useState(false)
    const { backendUrl, setIsLoggedIn, getUserData } = useContext(AppContext)
    const navigate = useNavigate();

    const onSubmitHandler = async (event) => {
        event.preventDefault();
        axios.defaults.withCredentials = true;
        setLoading(true)
        try {
            if (isCreateAccount) {
                // Registra API 
                const response = await axios.post(`${backendUrl}/register`, { name, email, password });
                if (response.status === 201) {
                    navigate("/");
                    toast.success("Account creato con successo.");
                } else {
                    toast.error("Email già registrata");
                }
            } else {
                // Login API
                const response = await axios.post(`${backendUrl}/login`, {  email, password });
                if (response.status === 200) {
                    setIsLoggedIn(true);
                    getUserData();
                    navigate("/");
                } else {
                    toast.error("Email o password errate");
                }
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
            setLoading(false);
        }
    }

    return (
        <div className="position-relative min-vh-100 d-flex justify-content-center align-items-center"
            style={{ background: "linear-gradient(90deg, #6a5af9, #8268f9", border: "none" }}
        >
            <div style={{ position: "absolute", top: "20px", left: "30px", display: "flex", alignItems: "center" }}>
                <Link to={"/"} style={{
                    display: "flex",
                    gap: 5,
                    alignItems: "center",
                    fontWeight: "500",
                    fontSize: "24px",
                    textDecoration: "none"
                }}>
                    <img src={assets.logo_home} alt="logo" height={32} width={32} />
                    <span className="fw-bold fs-4 text-light"> Authify</span>
                </Link>
            </div>
            <div className="card p-4" style={{ maxWidth: "400px", width: "100%" }}>
                <h2 className="text-center mb-4">
                    {isCreateAccount ? "Crea un account" : "Login"}
                </h2>
                <form onSubmit={onSubmitHandler}>
                    {
                        isCreateAccount && (
                            <div className="mb-3">
                                <label htmlFor="name" className="form-label"> Nome</label>
                                <input
                                    type="text"
                                    id="name"
                                    className="form-control"
                                    placeholder="Inserisci il nome"
                                    required
                                    onChange={(e) => { setName(e.target.value) }}
                                    value={name}
                                />
                            </div>
                        )
                    }
                    <div className="mb-3">
                        <label htmlFor="email" className="form-label"> Email</label>
                        <input
                            type="text"
                            id="email"
                            className="form-control"
                            placeholder="Inserisci l'email"
                            required
                            onChange={(e) => { setEmail(e.target.value) }}
                            value={email}
                        />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="password" className="form-label"> Password</label>
                        <input
                            type="password"
                            id="password"
                            className="form-control"
                            placeholder="************"
                            required
                            onChange={(e) => { setPassword(e.target.value) }}
                            value={password}
                        />
                    </div>
                    <div className="d-flex justify-content-between mb-3">
                        <Link to={"/reset-password"} className="text-decoration-none"> Password dimenticata? </Link>
                    </div>
                    <button type="submit" className="btn btn-primary w-100" disabled={loading}>
                        {loading ? "Caricamento.." : isCreateAccount ? "Registrati" : "Accedi"}
                    </button>
                </form>

                <div className="text-center mt-3">
                    <p className="mb-0">
                        {isCreateAccount ?
                            (
                                <>
                                    Hai già un account? {" "} <span
                                        className="text-decoration-underline"
                                        style={{ cursor: "pointer" }}
                                        onClick={() => setIsCreateAccount(false)}>
                                        Clicca qui
                                    </span>
                                </>
                            ) :
                            (
                                <>
                                    Non hai un account? {" "} <span
                                        className="text-decoration-underline"
                                        style={{ cursor: "pointer" }}
                                        onClick={() => setIsCreateAccount(true)}>
                                        Clicca qui
                                    </span>
                                </>
                            )
                        }
                    </p>
                </div>
            </div>
        </div>
    )
}