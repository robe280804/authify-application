import { Link } from "react-router-dom"
import { assets } from "../assets/assets"
import { useState } from "react"

export const Login = () => {
    const [isCreateAccount, setIsCreateAccount] = useState(true);

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
                <form>
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
                        />
                    </div>
                    <div className="d-flex justify-content-between mb-3">
                        <Link to={"/reset-password"} className="text-decoration-none"> Password dimenticata? </Link>
                    </div>
                    <button type="submit" className="btn btn-primary w-100">
                        {isCreateAccount ? "Registrati": "Login"}
                    </button>
                </form>
                
                <div className="text-center mt-3"></div>
            </div>
        </div>
    )
}