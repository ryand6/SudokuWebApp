import { useState, useEffect } from 'react';

export function useDotsAnimation(interval = 500): number {
    const [dotsCount, setDotsCount] = useState(0);
    useEffect(() => {
        const timer = setInterval(() => {
            setDotsCount(prevCount => (prevCount + 1) % 4);
        }, interval);
        return () => clearInterval(timer); 
    }, [interval]);

    return dotsCount;
    // switch (dotsCount) {
    //     case 0:
    //         return '';
    //     case 1:
    //         return '.';
    //     case 2:
    //         return '..';
    //     default:
    //         return '...';
    // }
};