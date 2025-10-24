package eu.ageekatyourservice.vadinvoicing;

import eu.ageekatyourservice.vadinvoicing.entity.InterventionLog;
import eu.ageekatyourservice.vadinvoicing.entity.User;
import eu.ageekatyourservice.vadinvoicing.repository.InterventionLogRepository;
import eu.ageekatyourservice.vadinvoicing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private InterventionLogRepository logRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private static final DateTimeFormatter FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public void run(String... args) throws Exception {
        // Create default users
        User admin = new User(null, "admin", passwordEncoder.encode("admin"), "ROLE_ADMIN");
        User user = new User(null, "user", passwordEncoder.encode("user"), "ROLE_USER");
        
        userRepository.save(admin);
        userRepository.save(user);
        
        System.out.println("Created users:");
        System.out.println("  - admin / admin");
        System.out.println("  - user / user");
        
        // Load sample intervention logs
        loadSampleLogs();
        
        System.out.println("Loaded " + logRepository.count() + " intervention logs");
    }
    
    private void loadSampleLogs() {
        String[] logData = {
            "2025-04-04 15:10:02|1315177977|krs-quentin@ad|Intervention sur 177510031 de 484 s|484|900",
            "2025-04-09 11:38:24|1315177977|krs-quentin@ad|Intervention sur 177510031 de 9048 s|9048|9900",
            "2025-04-09 11:48:54|1315177977|krs-20241103@ad|Intervention sur 1835039802 de 85 s|85|900",
            "2025-04-09 15:27:07|1315177977|krs-20241103@ad|Krs Logistics : Imprimantes Karolos|94|900",
            "2025-04-15 18:26:29|1315177977|krs-angelique@ad|Intervention sur 1660472302 de 1013 s|1013|1800",
            "2025-04-17 10:45:14|1315177977|krs-20241105@ad|KRS Logistics: Dépannagé Bénédicte|267|900",
            "2025-04-17 10:50:23|1315177977|934727118|KRS Logistics: Dépannage Angel - WPS Office|215|900",
            "2025-04-18 09:44:35|1315177977|674467009|KRS Logistics: Jean Michel Aiméblanc, Nettoyage Boite Gmail|4123|4500",
            "2025-04-18 14:00:33|1315177977|674467009|KRS Logistics: Nettoyage Boite Gmail - JM|793|900",
            "2025-04-21 23:45:15|1315177977|krs-20250401@ad|Intervention sur 1688865493 de 595 s|595|900",
            "2025-04-22 07:46:21|1315177977|krs-20250401@ad|Intervention sur 1688865493 de 3127 s|3127|3600",
            "2025-04-22 07:48:43|1315177977|krs-20250402@ad|Intervention sur 1355347042 de 8672 s|8672|9000",
            "2025-04-22 08:37:33|1315177977|krs-20250201@ad|Intervention sur 1170409273 de 191 s|191|900",
            "2025-04-22 08:39:11|1315177977|krs-20250401@ad|Intervention sur 1688865493 de 98 s|98|900",
            "2025-04-22 08:42:41|1315177977|krs-des-mela@ad|Intervention sur 1957812369 de 5579 s|5579|6300",
            "2025-04-22 08:43:54|1315177977|krs-20250401@ad|KRS Logistics: réinstallation PC Stéphanie|657|900",
            "2025-04-22 09:09:54|1315177977|krs-20250401@ad|Krs Logistics: Nouveau PC Stéphanie|158|900",
            "2025-04-22 11:26:32|1315177977|krs-20250401@ad|Krs Logistics: Imprimantes Stephanie|1253|1800",
            "2025-04-22 13:06:29|1315177977|printserver-390@ad|Intervention sur 242044904 de 13169 s|13169|13500",
            "2025-04-23 14:17:37|1315177977|674467009|Intervention sur 674467009 de 2424 s|2424|2700",
            "2025-04-23 15:25:49|1315177977|krs-20250401@ad|Intervention sur 1688865493 de 147 s|147|900",
            "2025-04-23 15:28:24|1315177977|krs-20250402@ad|Krs Logistics: Quentin, Sumatra PDF + Mot de passe o365|194|900",
            "2025-04-23 16:17:57|1315177977|krs-20240803@ad|Krs Logistics; Gwen Mail de RRR and C|581|900",
            "2025-04-25 11:22:47|1315177977|krs-zoe-tower@ad|Krs Logistics: Machine Zoe pas assez puissante devra être remplacée|320|900",
            "2025-04-25 14:11:01|1315177977|krs-zoe-tower@ad|Krs Logistics: Zoe Probleme Chrome|197|900",
            "2025-04-28 09:48:59|1315177977|krs-20240801@ad|Intervention sur 1525852960 de 344 s|344|900",
            "2025-04-29 10:11:14|1315177977|krs-lap-jm-01@ad|Krs Logistics: Logicel d'accès à distance pour la banque lux.|109|900",
            "2025-04-29 15:57:13|1315177977|krs-des-mela@ad|Krs Logistics: Accès Payable via Navigateur Web (Martine)|265|900",
            "2025-04-29 17:39:50|1315177977|printserver-390@ad|Krs Logistics: Intranet|3655|4500",
            "2025-04-30 10:55:56|1315177977|krs-lap-jm-01@ad|Krs Logistics: Problème de surcharge du fichier OST, réduction à 2 semaines de cache local|550|900",
            "2025-05-12 22:31:39|1315177977|674467009|Krs Logistics: Nettoyage Calendrier JM pour Virus|224|900",
            "2025-05-15 16:33:03|1315177977|krs-20250401@ad|KRS Logistics: Aide Stéphanie Acces MultiLine Bank|342|900",
            "2025-05-21 08:39:23|1315177977|krs-thomas@ad|Krs Logistics: Dépannage Office Thomas Dario|886|900",
            "2025-05-22 14:43:01|1315177977|krs-20240803@ad|Intervention sur 1060876485 de 503 s|503|900",
            "2025-05-23 10:45:41|1315177977|printserver-390@ad|Intervention sur 242044904 de 7465 s|7465|8100",
            "2025-05-23 15:25:53|1315177977|674467009|Krs Logistics: Dépannage Jean Michel|542|900",
            "2025-06-02 15:39:21|1315177977|674467009|Krs Logistics: Jean Michel, Perte Imprimante Bureau, Réinstallation|579|900",
            "2025-06-03 09:01:42|1315177977|krs-20241105@ad|KRS Logistics: Dépannage Bénédicte, Boite Email Saturée, Changement de l'archivage de 150 à 100 jours, nettoyage des 3 dossiers inutiles. (15 Min)|315|900",
            "2025-06-03 13:56:31|1315177977|printserver-390@ad|KRS Logistics: Recherche pertes emails|1813|2700",
            "2025-06-03 14:27:14|1315177977|krs-20250401@ad|KRS Logistics: Dépannage, explication & analyse de la \"perte\" des emails de Payables avec Stéphanie|1045|1800",
            "2025-06-04 11:02:28|1315177977|674467009|Intervention sur 674467009 de 1228 s|1228|1800",
            "2025-06-04 15:50:19|1315177977|krs-thomas@ad|Krs Logistics: Nettoyage des régles Outlook Thomas Dario|586|900",
            "2025-06-04 15:53:41|1315177977|krs-20250201@ad|Krs Logistics: Nettoyage des régles Outlook pour Sandrine|356|900",
            "2025-06-11 15:21:12|1315177977|674467009|Krs Logistics: Dépannage Email Jean Michel|988|1800",
            "2025-06-12 13:18:17|1315177977|printserver-390@ad|Krs Logistics: Liens du backup du NAS perdu.Il est nécéssaire de le reactiver, besoin de Jean Michel pour valider la MFA.|1697|1800",
            "2025-06-13 15:39:05|1315177977|krs-lap-jm-01@ad|Krs Logistics: Dépannage Email Jean Michel|2033|2700",
            "2025-06-13 16:00:31|1315177977|674467009|Krs Logistics: Dépannage Jean Michel|711|900",
            "2025-06-15 17:11:01|1315177977|printserver-390@ad|Krs Logistics: Mise à jour du firewall 1/2|8612|9000",
            "2025-06-15 19:50:12|1315177977|printserver-390@ad|Krs Logistics: Mise à jour Firewall 2/2|1046|1800",
            "2025-06-17 11:36:31|1315177977|printserver-390@ad|Krs Logistics: Supression Définitives des boites de Karolos, Francesco & Mélanie.Adaptation des couts de licences O365.|648|900",
            "2025-06-17 11:55:54|1315177977|printserver-390@ad|Krs Logistics: Recherche et téléchargement des factures non recues par Email|2919|3600",
            "2025-06-17 12:18:26|1315177977|674467009|Krs Logistics: jean Michel, Modification des comptes de Facturation Microsoft Office 365|1531|1800",
            "2025-06-18 08:28:17|1315177977|krs-20241103@ad|Krs-Logistics: Zoe Spineux, initialisation du Profile.L'ancienne machine a rendu l'ame.Remplacée par la machine de Karolos.Il faut récupérer les données de Zoe.|1040|1800",
            "2025-06-24 14:28:12|1315177977|674467009|Intervention sur 674467009 de 154 s|154|900",
            "2025-06-24 14:32:03|1315177977|674467009|Krs Logistics: Imprimantes Jean Michel, Factures O365, Carte de Crédits (Premier Essai)|3508|3600",
            "2025-06-27 15:41:08|1315177977|674467009|Krs Logistics: Changement de carte de paiement|1005|1800",
            "2025-07-02 10:43:08|1315177977|krs-lap-jm-01@ad|Krs Logistics: Installation des dossiers partagés pour JM, & Antivirus|527|900",
            "2025-07-03 10:48:09|1315177977|krs-20241102@ad|Krs Logistics: Installation de la machine de Justine Remy, Création du compte O365, Attributions des droits & Antivirus|7518|8100",
            "2025-07-03 14:37:55|1315177977|krs-20241102@ad|Krs Logistics: Accès NAS Justine Remy, et création des accès à Transport|5825|6300",
            "2025-07-04 09:59:42|1315177977|krs-20241102@ad|Krs Logistics: Vérification de la machine de Justine Remy avec Ludo & Accès à Transport (KRS Logistics)|197|900",
            "2025-07-09 15:43:11|1315177977|krs-des-05@ad|Krs Logistics: Thierry Dario, Expiration Compte, Activation du MFA|1175|1800",
            "2025-07-09 16:13:40|1315177977|krs-des-05@ad|Krs Logistics: Accès NAS|289|900",
            "2025-07-09 16:18:54|1315177977|frederic-45@ad|Krs Logistics: Fred M., Authentification Outlook, MFA|138|900",
            "2025-07-09 16:35:11|1315177977|krs-20250202@ad|Krs Logistics: Mot de passe Ludo, Authenticator|590|900",
            "2025-07-10 14:30:53|1315177977|krs-des-06@ad|Intervention sur 660230034 de 626 s|626|900",
            "2025-07-14 13:40:50|1315177977|674467009|Krs Logistics: Jean Michel, Activation du MFA Authenticator pour l'adresse Spédition.|935|1800",
            "2025-07-16 10:06:44|1315177977|1241631286|Krs Logistics:Thierry Dario, Probleme d'accès Email|384|900",
            "2025-07-16 10:13:35|1315177977|1241631286|Krs Logistics: Thierry Dario, Accès Exchange sur Mac|1709|1800",
            "2025-07-22 09:40:41|1315177977|krs-des-fred@ad|Intervention sur 1393335517 de 2445 s|2445|2700",
            "2025-07-22 10:26:40|1315177977|krs-des-fred@ad|Krs Logistics: Frederic, Soucis d'espace disque, la machine semble instable le disque dur ou la mémoire vive semblent abimés.|1375|1800",
            "2025-07-22 11:30:29|1315177977|krs-des-fred@ad|Krs Logistics: Ordinateur de Fred Lent, changement de logiciel|6562|7200",
            "2025-08-05 10:36:48|713842088|krs-20250402@ad|Krs Logistics: Dépannage Quentin Grandjean, Boite Email Locale Pleine|485|900",
            "2025-08-22 13:38:09|1315177977|krs-20240801@ad|Intervention sur 1525852960 de 78 s|78|900",
            "2025-09-08 08:42:21|1315177977|krs-20241105@ad|Krs-Logistics: Zohira Jabri, Adaptattion du poste, mise à jour, accès NAS (Time: 90 Minutes)|2044|2700",
            "2025-09-09 15:23:42|1315177977|674467009|Krs Logistics: Aide jean Michel Aimeblanc, Factures et Carte Visa|558|900",
            "2025-09-12 15:51:32|713842088|674467009|Krs Logistics: Windows Hello Jean Michel|131|900",
            "2025-09-22 15:09:56|1315177977|674467009|KRS Logistics: Dépannage Jeam Michel, accès Boite Email|536|900",
            "2025-09-30 14:26:54|1315177977|krs-laptop-01@ad|KRs Logistics: Dépannage Valentin suite à déplacement des PCs|191|900",
            "2025-10-13 14:15:56|1315177977|krs-20250202@ad|KRS Logistics: Ludo probleme mot de passe|3519|3600",
            "2025-10-13 14:21:32|1315177977|krs-20250401@ad|KRS Logistics: Stéphanie reset mot de passe + MFA|3202|3600"
        };
        
        for (String line : logData) {
            String[] parts = line.split("\\|");
            if (parts.length == 6) {
                InterventionLog log = new InterventionLog();
                log.setTimestamp(LocalDateTime.parse(parts[0], FORMATTER));
                log.setClientId(parts[1]);
                log.setUsername(parts[2]);
                log.setDescription(parts[3]);
                log.setDuration(Integer.parseInt(parts[4]));
                log.setBilledDuration(Integer.parseInt(parts[5]));
                
                logRepository.save(log);
            }
        }
    }
}
