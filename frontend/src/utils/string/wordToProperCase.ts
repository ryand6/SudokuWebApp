export function wordToProperCase(word: string): string {
    word = word.trim();
    return word[0].toUpperCase() + word.slice(1).toLowerCase();
}