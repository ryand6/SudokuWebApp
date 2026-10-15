import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useGetCurrentUser } from "../api/rest/users/query/useGetCurrentUser";
import { redirectPostLogin } from "../utils/routing/redirectPostLogin";
import { useIsMobile } from "@/hooks/global/useIsMobile";
import { HomeMobileLayout } from "@/components/home/HomeMobileLayout";
import { HomeDesktopLayout } from "@/components/home/HomeDesktopLayout";

export function HomePage() {
    const { data: user } = useGetCurrentUser();
    const navigate = useNavigate();
    const isMobile = useIsMobile();

    const [isImageLoading, setIsImageLoading] = useState(true);
    const [isImageError, setIsImageError] = useState(false);

    useEffect(() => {
        const img = new Image();
        img.src = "/images/developer.jpg";

        img.onload = () => {
            setIsImageLoading(false);
        };

        img.onerror = (e) => {
            setIsImageLoading(false);
            setIsImageError(true);
        };
    }, []);

    // When homepage is accessed, check if it was via a redirect following a login/account set up - if so, redirect to original URL
    useEffect(() => {
        redirectPostLogin(navigate)
    }, [user]);

    return (
        isMobile ? (
            <HomeMobileLayout user={user} navigate={navigate} isImageLoading={isImageLoading} isImageError={isImageError} />
        ) : (
            <HomeDesktopLayout user={user} navigate={navigate} isImageLoading={isImageLoading} isImageError={isImageError} />
        )
    )
}