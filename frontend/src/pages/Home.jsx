import { Header } from "../components/Header"
import { MenuBar } from "../components/MenuBar"

export const Home = () => {
    return (
        <div className="flex flex-col items-center justify-vontent-center min-vh-100">
           <MenuBar /> 
           <Header />
        </div>
        
    )
}