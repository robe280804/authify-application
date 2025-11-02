import { useContext } from "react"
import { assets } from "../assets/assets"
import { AppContext } from "../context/AppContext"

export const Header = () => {
const {userData} = useContext(AppContext)

    return (
        <div className="text-center d-flex flex-column align-items-center justify-content-center py-5 px-3" 
            style={{minHeight: "80vh"}}>
            
            <img src={assets.header} alt="header" width={120} className="mb-4" />

            <h5 className="fw-semibold">
                Ciao {userData ? userData.name : "Developer"} <span role="img" aria-label="wave">👋</span>
            </h5>
            <h1 className="fw-bold display-5 mb-3"> Benvenuto nel nostro prodotto </h1>

            <p className="text-muted fs-5 mb-4" style={{maxWidth: "500px"}}>
                Iniziamo con un rapido tour del prodotto e potrai configurare l'autenticazione in pochissimo tempo!
            </p>

            <button className="btn btn-outline-dark rounded-pill px-4 py-2">
                Inizia
            </button>
        </div>
    )
}