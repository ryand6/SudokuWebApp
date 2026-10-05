export function AboutPage() {
    return (
        <div className="flex flex-col w-full h-full font-display overflow-y-auto">
            <div className="flex flex-col w-full max-w-5xl mx-auto px-6 md:px-10 py-8 md:py-14 gap-8">

                <section className="flex flex-col gap-3">
                    <h1 className="tracking-wide font-semibold text-2xl text-foreground">
                        About TomoSudoku
                    </h1>

                    <div className="flex flex-col gap-3 text-muted-foreground leading-relaxed">
                        <p>
                            TomoSudoku is an online multiplayer Sudoku site that started 
                            as a passion project to create a place people could play 
                            Sudoku together online without adverts, tracking, subscriptions 
                            or paywalls. The site is completely free to use, and I intend 
                            to keep it that way.
                        </p>

                        <p>
                            Privacy is central to TomoSudoku. Data used by the site is 
                            intentionally kept to an absolute minimum.
                            You can find more information in the{" "}
                            <a
                                href="/privacy-policy"
                                className="text-primary hover:underline"
                            >
                                Privacy Policy
                            </a>.
                        </p>
                    </div>
                </section>

                <section className="flex flex-col gap-4">
                    <h2 className="tracking-wide font-semibold text-xl text-foreground">
                        About the developer
                    </h2>

                    <div className="flex items-center gap-4">
                        <img
                            src="/images/developer.jpg"
                            alt="Ryan"
                            className="rounded-full w-[75px] h-[75px] object-cover"
                        />

                        <div className="flex flex-col gap-1">
                            <div className="font-semibold text-lg text-foreground">
                                Ryan
                            </div>

                            <div className="text-muted-foreground tracking-wide">
                                Full-stack developer
                            </div>

                            <a
                                href="https://github.com/ryand6"
                                target="_blank"
                                rel="noopener noreferrer"
                                className="mt-1"
                            >
                                <img
                                    src="/images/GitHub_Invertocat_Black_Clearspace.png"
                                    alt="GitHub"
                                    className="rounded-full w-[30px] h-[30px]"
                                />
                            </a>
                        </div>
                    </div>

                    <div className="flex flex-col gap-3 text-muted-foreground leading-relaxed">
                        <p>
                            If you enjoy the site and would like to support the project,
                            you can{" "}
                            <a
                                href="https://buymeacoffee.com/ryand6"
                                target="_blank"
                                rel="noopener noreferrer"
                                className="text-primary font-semibold hover:underline"
                            >
                                buy me a coffee
                            </a>.
                            Any contribution helps with the costs of keeping the site
                            running and gives me more time to expand and explore new ideas.
                        </p>
                    </div>
                </section>

                <section className="flex flex-col gap-3">
                    <h2 className="tracking-wide font-semibold text-xl text-foreground">
                        Get in touch
                    </h2>

                    <div className="flex flex-col gap-3 text-muted-foreground leading-relaxed">
                        <p>
                            I'm always interested in feedback, suggestions and ideas
                            for TomoSudoku. If you have something you'd like to suggest,
                            find a problem, or just want to get in touch, you can email
                            me at{" "}
                            <a
                                href="mailto:tomosudoku@gmail.com"
                                className="text-primary hover:underline"
                            >
                                tomosudoku@gmail.com
                            </a>.
                        </p>

                        <p>
                            Thanks for checking out TomoSudoku!
                        </p>
                    </div>
                </section>

            </div>
        </div>
    );
}