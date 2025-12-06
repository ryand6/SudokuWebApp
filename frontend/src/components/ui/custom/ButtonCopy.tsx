import { IconCheck, IconCopy } from '@tabler/icons-react';
import { Button } from '../button';
import { useEffect, useState } from 'react';

interface ButtonCopyProps {
    text: string | null;
    className?: string; 
}


export function ButtonCopy({ text, className }: ButtonCopyProps) {
    const [copied, setCopied] = useState(false)

    useEffect(() => {
        setCopied(false);
    }, [text]);

    const handleCopy = async (text: string | null) => {
        if (text === null) return;
        try {
            await navigator.clipboard.writeText(text);
            setCopied(true);
            // resets after 3 seconds
            setTimeout(() => setCopied(false), 3000);
        } catch (err) {
            console.error("Copy failed: ", err);
        }
    };

    return (
        <Button 
          variant="outline" 
          size="sm" 
          onClick={() => handleCopy(text)} 
          disabled={!text} 
          className={`flex items-center gap-1 cursor-pointer ${className || ""}`} 
        >
          {copied 
          ? <span className="flex items-center gap-1"><IconCheck size={16} /> Copied!</span>
          : <span className="flex items-center gap-1"><IconCopy size={16} /> Copy</span>}

        </Button>
    );
}