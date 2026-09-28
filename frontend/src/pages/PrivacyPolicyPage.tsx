import { IconCookie, IconDatabase, IconKey, IconMail, IconShieldLock, IconUsers } from "@tabler/icons-react";
import { Separator } from "@/components/ui/separator";
import { Section } from "@/components/global/Section";
import { useIsMobile } from "@/hooks/global/useIsMobile";
import { BulletList } from "@/components/global/BulletList";

const CONTROLLER_NAME = "Ryan Downey";
const CONTACT_EMAIL = "tomosudoku@gmail.com";
const LAST_UPDATED = "27 September 2026";

export function PrivacyPolicyPage() {
    const isMobile = useIsMobile();

    const iconSize = isMobile ? 18 : 36;

    return (
        <div className="flex min-h-full flex-col bg-background font-display tracking-wide">
            <header className="flex flex-col bg-sidebar text-sidebar-foreground">
                <div className="mx-auto flex w-full max-w-4xl items-center gap-4 px-5 py-6">
                    <span className="flex shrink-0 items-center justify-center rounded-2xl bg-background/20 p-3 sm:p-4">
                        <IconShieldLock size={iconSize} />
                    </span>
                    <div className="flex flex-col gap-1">
                        <h1 className="text-2xl font-semibold tracking-wider sm:text-4xl">
                            Privacy Policy
                        </h1>
                        <p className="text-sm tracking-wider text-primary-foreground/80 sm:text-base">
                            Last updated {LAST_UPDATED}
                        </p>
                    </div>
                </div>
            </header>

            <main className="flex-1">
                <div className="mx-auto flex w-full max-w-4xl flex-col gap-8 px-5 py-7 sm:px-8 sm:py-10">
                    <div className="flex flex-col gap-8 rounded-2xl border border-muted bg-card p-5 sm:p-8">
                        <Section
                            icon={IconShieldLock}
                            title="1. Who we are"
                            isMobile={isMobile}
                        >
                            <p>
                                TomoSudoku is operated by{" "}
                                {CONTROLLER_NAME}.
                            </p>
                            <p>
                                If you have questions about this Privacy
                                Policy or your personal data, contact us at{" "}
                                <a
                                    href={`mailto:${CONTACT_EMAIL}`}
                                    className="font-semibold text-primary underline-offset-4 hover:underline"
                                >
                                    {CONTACT_EMAIL}
                                </a>
                                .
                            </p>
                            <p>
                                TomoSudoku is operated from the United Kingdom.
                            </p>
                        </Section>

                        <Separator />

                        <Section
                            icon={IconDatabase}
                            title="2. What we collect and why"
                            isMobile={isMobile}
                        >
                            <p>
                                We collect and use only the information needed
                                to operate TomoSudoku.
                            </p>
                            <BulletList>
                                <li>your chosen username;</li>
                                <li>a cryptographic hash of your recovery email address;</li>
                                <li>your OAuth login provider and provider account ID when you sign in with Google, GitHub or Facebook;</li>
                                <li>game results, scores, statistics and rankings; and</li>
                                <li>limited technical information needed for authentication, security and operation.</li>
                            </BulletList>
                            <p>We use this information to:</p>
                            <BulletList>
                                <li>provide and manage your account;</li>
                                <li>authenticate you;</li>
                                <li>provide multiplayer games;</li>
                                <li>maintain scores, statistics and leaderboards;</li>
                                <li>support account recovery; and</li>
                                <li>keep TomoSudoku secure and functioning.</li>
                            </BulletList>
                            <p>
                                We do not use your personal data for
                                advertising, behavioural profiling or
                                third-party analytics.
                            </p>
                        </Section>

                        <Separator />

                        <Section
                            icon={IconUsers}
                            title="3. Information visible to other players"
                            isMobile={isMobile}
                        >
                            <p>
                                Your username and relevant gameplay
                                information may be visible to other players
                                through multiplayer games, leaderboards,
                                rankings and statistics.
                            </p>
                            <div className="rounded-xl border border-primary/30 bg-primary/5 p-4">
                                <p className="font-semibold text-primary">
                                    Please do not use your real name, email
                                    address, location, school or other
                                    personal information as your username.
                                </p>
                            </div>
                        </Section>

                        <Separator />

                        <Section
                            icon={IconKey}
                            title="4. Third parties"
                            isMobile={isMobile}
                        >
                            <p>
                                If you sign in using Google, GitHub or
                                Facebook, the relevant provider processes
                                information as part of providing its
                                authentication service. We do not store your
                                password for these services.
                            </p>
                            <p>
                                We may also use hosting, database and other
                                service providers to operate TomoSudoku.
                                These providers may process personal data on
                                our behalf.
                            </p>
                            <p>
                                We do not sell or rent your personal data.
                            </p>
                        </Section>

                        <Separator />

                        <Section
                            icon={IconCookie}
                            title="5. Cookies"
                            isMobile={isMobile}
                        >
                            <p>
                                TomoSudoku uses cookies necessary for
                                authentication, security and operation of
                                the service.
                            </p>
                            <p>
                                We do not use advertising or
                                analytics cookies.
                            </p>
                        </Section>

                        <Separator />

                        <Section
                            icon={IconDatabase}
                            title="6. How long we keep your data"
                            isMobile={isMobile}
                        >
                            <p>
                                We keep personal data for as long as reasonably
                                necessary to provide and secure TomoSudoku.
                            </p>
                            <p>
                                Account and gameplay information is generally
                                kept while it is needed for your account, game
                                history, statistics and related features.
                            </p>
                            <p>
                                When information is no longer needed, we will
                                delete or anonymise it where reasonably
                                practicable, unless we are required or
                                permitted to keep it for longer.
                            </p>
                        </Section>

                        <Separator />

                        <Section
                            icon={IconShieldLock}
                            title="7. Your rights"
                            isMobile={isMobile}
                        >
                            <p>
                                Depending on the law that applies to you, you
                                may have rights to:
                            </p>
                            <BulletList>
                                <li>access your personal data;</li>
                                <li>correct inaccurate data;</li>
                                <li>request deletion;</li>
                                <li>restrict or object to certain processing;</li>
                                <li>request a copy of your data where applicable; and</li>
                                <li>withdraw consent where we rely on consent.</li>
                            </BulletList>
                            <p>
                                You can exercise these rights by contacting{" "}
                                <a
                                    href={`mailto:${CONTACT_EMAIL}`}
                                    className="font-semibold text-primary underline-offset-4 hover:underline"
                                >
                                    {CONTACT_EMAIL}
                                </a>
                                .
                            </p>
                            <p>
                                You may also complain to the relevant data
                                protection authority. In the UK, this is the
                                Information Commissioner's Office (ICO).
                            </p>
                        </Section>

                        <Separator />

                        <Section
                            icon={IconUsers}
                            title="8. Children"
                            isMobile={isMobile}
                        >
                            <p>
                                TomoSudoku may be used by children and young
                                people. We aim to minimise the personal data
                                we collect and do not require users to provide
                                information such as their real name, date of
                                birth, home address or precise location.
                            </p>
                            <p>
                                If a parent or carer has a concern about a
                                child's personal data, they can contact us at{" "}
                                <a
                                    href={`mailto:${CONTACT_EMAIL}`}
                                    className="font-semibold text-primary underline-offset-4 hover:underline"
                                >
                                    {CONTACT_EMAIL}
                                </a>
                                .
                            </p>
                        </Section>

                        <Separator />

                        <Section
                            icon={IconMail}
                            title="9. Changes and contact"
                            isMobile={isMobile}
                        >
                            <p>
                                We may update this Privacy Policy when our
                                service or data practices change. The date at
                                the top of this page shows when it was last
                                updated.
                            </p>
                            <div className="rounded-xl bg-muted/40 p-4">
                                <p className="font-semibold">
                                    {CONTROLLER_NAME}
                                </p>
                                <p>Tomo Sudoku</p>
                                <p>
                                    <a
                                        href={`mailto:${CONTACT_EMAIL}`}
                                        className="font-semibold text-primary underline-offset-4 hover:underline"
                                    >
                                        {CONTACT_EMAIL}
                                    </a>
                                </p>
                            </div>
                        </Section>
                    </div>
                </div>
            </main>
        </div>
    );
}

