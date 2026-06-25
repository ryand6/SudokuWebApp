import { wordToProperCase } from "../string/wordToProperCase";

export const durationOptions: { label: string; value: string }[] = [
        { label: "Quick", value: "15 min" },
        { label: "Standard", value: "30 min" },
        { label: "Marathon", value: "60 min" },
    ];

export const getDurationValue = (label: string) => {
    label = wordToProperCase(label);
    if (label === "UNLIMITED") {
        return "Unlimited";
    }
    const value = durationOptions.find(el => el.label === label)?.value;
    return value ? `${value}` : '';
}