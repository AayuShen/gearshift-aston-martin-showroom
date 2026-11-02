package com.gearshift.app.data;

import com.gearshift.app.data.Vehicle.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Static showroom catalogue used as demo data. */
public final class Inventory {

    private static final String IMG = "https://lh3.googleusercontent.com/aida-public/";


    public static final String HERO_IMAGE = IMG + "AB6AXuBvXUUvMQxxJ3MzCz7zxlcRh5dW2VnkEsaXE2y2wH7FhJqt69hiWVyFQahoUY79oEFbU3YR0d6tvX5Qznz-VFnWXkwHrJ5a4NFp2WKhuHH7CfxPIMMNxa3a_508WuJfJy2QIEtsLopWEhDPO7Tf21KTjmsoWuXXpj6TVRt4rKbuCEanx0g51UPE69kwCfUmGRjhxKXCMRtVDWxh6nEMrfxml3MUlUTNb-YZF-8wWw";

    static final String DB12_GREEN = IMG + "AB6AXuCsfrlcxEfGkvnKZ7uM5-7NKPPPvl6_4CnsW5aicywR-HKPFGTsBVykhCWkdUPMRuGRfMrL2z2tYRZu6C7kn_7K5iOCG_cpjGspv2kljREuTMfOxhQaviQ1bE3Y-8EME5Hn78mboObmDd7fEXScoZtFoTULqFmOfHUdh_H6qeIhkk4rYwuyMVA3MtdxvgkQKo_zZZ9M-66QbainstL_vZWPguE0vCtwx8yRUqa2Rg";
    static final String DB12_DETAIL_MAIN = IMG + "AB6AXuBZlEwRxtKvkcKvn_n1F4ZpnYMj-21-h-yGoTiD9NIGWsKNmKQh40x21eyG55WJCeDHyW4IG_z7f9hPaHvkxmMxBC4IiloXzWZCYmE1WVuAM3bJolWc2e-UT2aEfdCVnfwKdQTqwmqzMUI5ue2SHFo55iv2ita_GuM8EFhxP35Ea8ESC4PX1TjdIfluVNZtJ39IULZfkpI2qlpYqOLUahZP9r2xhlxXqmnrn3MKeQ";
    static final String DB12_DETAIL_FRONT = IMG + "AB6AXuDp5IFfESHEKz7133N3LTO11QZZ8HHRZvPp5cgnezV37H0namnOq9Nc9PUroVwwZeiOYIXKYcKzRqSefZFhJQVbxSFI70WuWDinPncftj49vZ4_ejO3WlBMeNfOla2FweYQtG8WInJXufbXuF2yeDSbZN5PK8CEgUKPy3uPQGTfZl9JdX9zbqnmkAhfZSpvV4Wq_OY8cMIwx6g6wxjnHWKhIG-IMZ75v1Krxaxn2A";
    static final String DB12_DETAIL_SIDE = IMG + "AB6AXuAf6weMiUaF2wJcAN3_jeoLZGvtGDiTONFwxmLPn93-9czeYCgD7FObwlYe1CeFzzgUUq31k1KUAJ6AUgijJqE5UdFTZS-MTm7CXf0N1FJVQO2Y1bCppA23Dhzi52BZ1vg0NgQ23dAjo4y1Q2numkJwLFhBCTbgJ-M5wF2pzWBFxSbTE3m1m7PTHSesJ2qDfETgx_eT8zuGU8NWSJcdXR_AR_gKHsx80g7dnwxNrQ";
    static final String DB12_DETAIL_CABIN = IMG + "AB6AXuDOB2UWrs9Y6pTWmjKgJnO5Wn3Wfd7uLb0y2V955TdPPW5dfd3OHRaBA_g65LofXRMFRz1i1CRPYhOB6y5_04KCBoYvF658GR5bvczc_DsUV4K9Z9LOu33sEMXNkb1Xfz3r2WHt_H5myfCZRYX3g09cZ7lwY6zwRgUZmtaPt5mZzGH2BLtFeSSUUa0XPAWyfOcigm_aeHY_pShO84lc9E60dR5Q3OHD4B_DrWWJmg";
    static final String DB12_DETAIL_COCKPIT = IMG + "AB6AXuAFS3U35HCc8VDW1kYqoE6LbcIqRYM2D8A3PRSNbPGn-fL7cvvsqdKmBTYrIYdameK80vMsCRxtUQzaH0ztjI8kQCXAPTE4uFSuUeNlbSg4MJH8FPWlrxTc3-rxLPdTjkwTt2xKHe3x8OzrylKzFL4nsHQz765PTFncIvKD6kqzl7hG6d0XsWsam3G3BPhTjA-rzwXzgZ9n_UeytQ7V-N_luNaw6xDVBjSCwFu82Q";
    static final String DB12_BRONZE = IMG + "AB6AXuDu5nNgfYjcFHaZR02U2ciczat0pEb-7Petg2_eBn-dM9qbSLlyX-xggs8CUJs6vqNj1kCxxBgkB1-zJzHn0kwaDIloFdXhT1tB3S-otbRQ0JGKF4XJK-H7BoN6Xq1sdnuXDH4NtCGXj7HHzuZzU91LLv2TSZJavAA-ro4W0bRBIO26afxVGlWUvnV_59cieTPsvhXO-L45FEyKze_8nQYEQOrJO546H0e_CUOQxA";
    static final String DB12_VOLANTE = IMG + "AB6AXuA6cXzTc4D0hnjlNuroElCXnuTDcYoyXr60WKlfUV5eXCLQ6-azThY6wgMSCOO9xTAkmnMV8WEVDEG04v6uEZv403GcyZ4HpD-EmoxLKiaglyNsR6s_iEYHqnvsj6-h--pC4vKMVs3Hn7KM7mdSXfwzozlVdtRCivzaJzeAJqZjj6fpS6jGmpgM3vo0VM_-b1UDK5FEAO8YOU9GPoLAxWT1Jn7JK9ewlCP0lKRX-A";
    static final String VANTAGE_PODIUM = IMG + "AB6AXuBknl-S8RN7lDCyTOI7uhV2C_Kt22LhA2AoTyOYs7tvbiviDo_uXFniX3_I7Vxa8pLZhERg0xstXJexQAGY_LIbJrpfo_TJ8_fnP0B6dZEkIsr2OU1jJAZawpYETvTlnLzV4qcepaBxidAxJ5JSfOy5LDKvxOl3uncASpFoqRHTyAj-vwgMgWpYqPjYCiZJ2VlSbvpAFVxxV3cD9F7nEuhQYO3I6HiogjasTCAFPQ";
    static final String VANTAGE_SIDE = IMG + "AB6AXuC7QaimOuK3gFcABrKByfn-AgZP2cfEOdgrDTawwv1UNp0FG9zC7_khBmohIn6RnWB8cvLRLfyzKdL807-3PMmUyPO_Y-2bTrNAqG0wpk6csAYyrM9wzZUSyicGcgR80FpWKV2_CD20vuRyF6-0mame_IXOFfrTL2E5bqmEnl0bzyPxIpiqBcimf1GIxh1ZSwkyUWl_SGm_5H2cqukwYe0cULdLQh5Mfj9d9CoGxA";
    static final String VANTAGE_F1 = IMG + "AB6AXuBzUWBPFs62kT7IDL9MwHfS5K8nSbWnI7QeE2GB93dbz-pHv-7pd0-k_aSDd_zVd4Cpqia3YZcvZblbmpebRVbiyoSg39KHc69P8FF8INt5OeIZG-sgQc1KP2eK2vhZXGaSu0jVwPMs2Tkme0-Q4Oq7V0AS_VVyk94trsNUtEaShe2JqgSKRE_aj3bjlhRy7BVgTq8K-7NbLcVMP528yj5D7aiqEjiTMYpxTDjqzw";
    static final String VANTAGE_YELLOW = IMG + "AB6AXuDOYilrvXVKiSxe7rCCDtdP4wFzp8hzx47scbzZChEUd_jym17M9HbfXV_mYI_WLuNOAqjm3Yv8y5GIgUDMHR6Og-GnSChvk-kcImSFtsYALro9b7UXam0tcwJ3CphbZg588F79bXYEgd9MkGMejjKhTBh8kS07HdM0oxgU1mnj-Risa9KrsMiOUhziQyAINGUvFEkPVbfc9k2OO9j0INdPU756hJnn3bI6UEn8qw";
    static final String DBX707_ONYX = IMG + "AB6AXuB2FjnECxJFBx133b0218itCBcIo3Do0-o2BbUwIGxC9dJENoMI3416-oV8EW3Ifp4hjhRcGn3dWmCP9JcMiQkUYjE3jcoLYRBWgpg4fxp-qcI0aMJkY0M8dnWUj1gmBK949BOlKFywqBJwn2XcMZRpX5-iKzmAaZGZAz0gD6Q_pxL1PNmATRcYgyToMeODXhDu-jcdgr7hI6BNFAOH02EzGJMjqdJahgFDWeHsBA";
    static final String DBX707_HIGHLANDS = IMG + "AB6AXuCZ0BuLtOkrQNrPv-_rbd0XJtRavS4eiInjzrW_-SI2GF6EePw5c0hGAglyhUxMmDBieGp753cL-qDP2m8TgxAFuCgg72ZOOiGyV62d2Z2SNy0UU-6LAIXl7OBn7VTdux_oKj8u09Zk-5vfGiADOAb3ZYtpz9qo5WdpQd7SN2mw24dwsHc38Cs0TpR6Yeqv4e2pjtfviFzJMewEHl9IlIywNSgOTRDq0l3fTqpq1w";
    static final String DBX_FOREST = IMG + "AB6AXuCZFo_gZCXsnvKQbm9K_SeYp3onfK6mpB1txWwnJlX0vntidM9d-wypC3skrCppUT43H-bsTTDZn3tEmHRujG4yWj-zfELxjZUNobfQbpe1ZKA-2gxk330DM2UAe7TgGhJOq666FpaqHikliVpQra799y425BEN5yxDGNALeZaKJkiARzd2G6mepGITFXgr8X-VSGJxe-PYngrI5PJ8AEarPEkI7iEkOmGRqGCN2g";
    static final String VANQUISH = IMG + "AB6AXuAViRy2_zSmqblwqqN6xItsFEqZHDafHKYy-3FTfMeDM3M3ofO3alwIl4l-OgpxKmx6VflCL_8RfjtTGBrr19SS6bkg4xXrP03UDby0grTrlmyM1iYlLKZ2rZSua7BqO_7FDGXN3ui7l-fp9aWS55HlxvTzHxdEdvpNwFCfHRDKVXd6bWQio0gjYIgVTm0AlDm5DZMa9_qNTtqVAheQh40C5NuRubFhhrnxEVwYBw";
    static final String DBS_REAR = IMG + "AB6AXuAqjbuPmmv-o1oE0GLVGWxP2MXXDEJF3h5HYSsr7afN4Sq1bQbykM3ogHel9uBvw-i9HsvsZr9b1mVZT9zUh5ldj_PjZLPvuZRDesEw0CntroAOcXXI--6nE0KKfXGKme4hAfVhfm_GDcNKEhktMnivaf9KOPkHBprUj5Wal9KRkmZTHGTv4NO2OA4rMOq0vBV4TpfIAOW6hujaTk0on7TYnFzBfvXhqQnICLjPdg";
    static final String DBS_770 = IMG + "AB6AXuClVZVE6Gprn9K8lR4au1bOY6s_NMSERQK2UGhzjsO6wobXUtBHNzXvLuJH9lzp9QUaXMII8M6JwwihOUUp0p9pn83k3iB4F92dGBLm0X-4ORbrj8tvCBB7S0hBi8aeeBEqtYXVLCd86UzQksb58Jy80R82ZgQ7yelPftDxTkylJp6kyn3WQc8Mu4dzMSzB8o28FKLkgVUYi1K9mMHP49ZdQkHS1oiJtn-SEjO0HQ";
    static final String VALHALLA = IMG + "AB6AXuDzhF3iGv7sgi8964Bq2yBZZ8Cy8f2lAsisWtT8wxFLOKhr66ioMpX6nbrBylq9vjScn6hObn6LwtPWWbw33OpZPoGC44WMcKH1wZubel4OmcZTPbv9LqwETueJaDb2dxLmRipmOG83ZwbkcY-x2vnAqdSV5gMzu0T6IgUnsyxTMxekUGPbQoTga5OvCOw17jLhA174HK45v1EqKrSlq7Lt_LMXlM-wswBuB-VihA";
    static final String VALKYRIE = IMG + "AB6AXuBEK9zNgx-Qe7OfwFnCHIj0HOYQHDXeT-6_5nYqSstCFcOIZgpGIIBezOGpt-DxKkV-IfwxOI459NXFmW1y_VZCPRU_razfHAjkbtHz82WwSOj0v4qfixe2MtzvaLyBKGNjoFL14oRLxKJELLDH7eBTEY6tt4knJxB1iSd0N3EtwZl9pbivznZCq5ZQ6J49vLUtYps_Jt-WOmRt3jrvvC4D9kZh3ODucrhCkd2m5w";

    private static final List<Vehicle> VEHICLES = new ArrayList<>();
    private static final List<ModelLine> MODEL_LINES = new ArrayList<>();

    static {
        VEHICLES.add(new Vehicle.Builder("db12-launch", "DB12", "DB12 Coupe")
                .edition("Launch Edition").series("Super Tourer Series").category(Category.TOURER).year(2024)
                .colour("Iridescent Emerald", "Iridescent Emerald · the DB12 launch colour")
                .price(45900, 650).status("Ready For Delivery").location("Aston Martin Mumbai, Kemps Corner")
                .commission("GS-1001").image(DB12_GREEN)
                .gallery(DB12_DETAIL_MAIN, DB12_DETAIL_SIDE, DB12_DETAIL_CABIN, DB12_DETAIL_COCKPIT)
                .performance(680, 800, "3.6", 325).drivetrain("4.0L Twin-Turbo V8", "ZF 8-Speed", "RWD")
                .odometer("420 km").weight(1685)
                .options("Q Satin Racing Green", "Bowers & Wilkins Audio", "Carbon Ceramic Brakes")
                .heritage().newDelivery().build());

        VEHICLES.add(new Vehicle.Builder("vantage-2024", "Vantage", "Vantage")
                .edition("2024 Generation").series("Pure Driver Series").category(Category.SPORTS).year(2024)
                .colour("Ultra Yellow", "Ultra Yellow · Black Exterior Pack")
                .price(39900, 565).status("Reserved").location("Aston Martin New Delhi, Mathura Road")
                .commission("GS-1002").image(VANTAGE_YELLOW).gallery(VANTAGE_YELLOW)
                .performance(665, 800, "3.4", 325).drivetrain("4.0L Twin-Turbo V8", "ZF 8-Speed", "RWD")
                .odometer("1,150 km").weight(1605)
                .options("Ultra Yellow Paint", "Semi-Aniline Onyx Interior", "21\" Satin Black")
                .newDelivery().build());

        VEHICLES.add(new Vehicle.Builder("vanquish-2025", "Vanquish", "Vanquish")
                .edition("Flagship").series("V12 Twin-Turbo Icon").category(Category.TOURER).year(2025)
                .colour("Xenon Grey Metallic", "Xenon Grey Metallic · Semi-Aniline Oxford Tan")
                .price(88500, 1250).status("Available").location("Aston Martin Mumbai, Kemps Corner")
                .commission("GS-1003").image(VANQUISH)
                .performance(835, 1000, "3.2", 345).drivetrain("5.2L Twin-Turbo V12", "ZF 8-Speed", "RWD")
                .odometer("Delivery").weight(1774)
                .options("Copper Ceramic Calipers", "Oxford Tan Leather", "Panoramic Roof")
                .heritage().newDelivery().build());

        VEHICLES.add(new Vehicle.Builder("dbx707-onyx", "DBX", "DBX707")
                .edition("Neutron White").series("Super SUV Benchmark").category(Category.SUV).year(2024)
                .colour("Neutron White", "Neutron White · Black Roof & 23\" Wheels")
                .price(46300, 655).status("Ready For Delivery").location("Aston Martin New Delhi, Mathura Road")
                .commission("GS-1004").image(DBX707_HIGHLANDS).gallery()
                .performance(707, 900, "3.3", 310).drivetrain("4.0L Twin-Turbo V8", "9-Speed Wet Clutch", "AWD Active")
                .odometer("Delivery").weight(2245)
                .options("Titanium Sport Exhaust", "23\" Forged Wheels", "Carbon Aero Pack")
                .newDelivery().build());

        VEHICLES.add(new Vehicle.Builder("vantage-f1", "Vantage", "Vantage F1 Edition")
                .edition("FIA Official Spec").series("Pure Performance").category(Category.SPORTS).year(2023)
                .colour("Satin Racing Green", "Satin Racing Green · Lime Aerodynamic Decals")
                .price(32000, 455).status("Verified").location("Aston Martin Mumbai, Kemps Corner")
                .commission("GS-1005").image(VANTAGE_F1).gallery(VANTAGE_F1).photoOnly()
                .performance(535, 685, "3.6", 314).drivetrain("4.0L Twin-Turbo V8", "8-Speed Automatic", "RWD Track")
                .odometer("3,200 km").weight(1570)
                .options("Fixed Rear Wing", "Lime Accent Livery", "Track Suspension")
                .heritage().build());

        VEHICLES.add(new Vehicle.Builder("db12-volante", "DB12", "DB12 Volante")
                .edition("Seychelles Blue").series("Convertible Grand Tourer").category(Category.TOURER).year(2024)
                .colour("Seychelles Blue", "Seychelles Blue · Saddle Tan Interior")
                .price(49500, 700).status("Special Order").location("Aston Martin New Delhi, Mathura Road")
                .commission("GS-1006").image(DB12_VOLANTE).photoOnly()
                .performance(680, 800, "3.7", 325).drivetrain("4.0L Twin-Turbo V8", "ZF 8-Speed", "RWD")
                .odometer("650 km").weight(1797)
                .options("K-Fold Roof", "Saddle Tan Leather", "Bowers & Wilkins Audio")
                .newDelivery().build());

        VEHICLES.add(new Vehicle.Builder("dbs-770", "DBS", "DBS 770 Ultimate")
                .edition("Minotaur Green").series("Final Edition").category(Category.SPECIAL).year(2023)
                .colour("Minotaur Green", "Minotaur Green · Satin Gold Wheels")
                .price(52500, 745).status("Verified").location("Aston Martin Mumbai, Kemps Corner")
                .commission("GS-1007").image(DBS_770).gallery(DBS_770, DBS_REAR)
                .performance(770, 900, "3.4", 340).drivetrain("5.2L Twin-Turbo V12", "ZF 8-Speed", "RWD")
                .odometer("1,800 km").weight(1770)
                .options("Satin Gold Multispoke", "Carbon Ceramic Brakes", "Final Edition Plaque")
                .heritage().build());

        VEHICLES.add(new Vehicle.Builder("db12-bronze", "DB12", "DB12 Coupe")
                .edition("Magneto Bronze").series("Super Tourer").category(Category.TOURER).year(2024)
                .colour("Magneto Bronze", "Satin Magneto Bronze · Obsidian Interior")
                .price(44500, 630).status("Available").location("Aston Martin New Delhi, Mathura Road")
                .commission("GS-1008").image(DB12_BRONZE).photoOnly()
                .performance(680, 800, "3.6", 325).drivetrain("4.0L Twin-Turbo V8", "ZF 8-Speed", "RWD")
                .odometer("900 km").weight(1685)
                .options("Satin Iridescent Paint", "Diamond-Turned Wheels")
                .build());

        VEHICLES.add(new Vehicle.Builder("dbx707-apex", "DBX", "DBX707")
                .edition("Apex Grey").series("Supercar of SUVs").category(Category.SUV).year(2024)
                .colour("Apex Grey", "Apex Grey · Carbon Diffuser")
                .price(44000, 620).status("Verified").location("Aston Martin Mumbai, Kemps Corner")
                .commission("GS-1009").image(DBX707_HIGHLANDS).gallery(DBX707_HIGHLANDS, DBX_FOREST).photoOnly()
                .performance(707, 900, "3.3", 310).drivetrain("4.0L Twin-Turbo V8", "9-Speed Wet Clutch", "AWD")
                .odometer("2,400 km").weight(2245)
                .options("Carbon Diffuser", "Black Pack Exterior")
                .heritage().build());

        VEHICLES.add(new Vehicle.Builder("valhalla", "Valhalla", "Valhalla")
                .edition("Limited Edition").series("Hybrid Supercar").category(Category.SPECIAL).year(2025)
                .colour("Apex Grey", "Apex Grey · Exposed Satin Carbon")
                .price(0, 0).status("Price on Request").location("Aston Martin New Delhi, Mathura Road")
                .commission("GS-1010").image(VALHALLA).gallery()
                .performance(950, 1100, "2.5", 350).drivetrain("4.0L V8 Plug-in Hybrid", "8-Speed DCT", "Mid-Engine AWD")
                .odometer("New").weight(1655)
                .options("Active Aerodynamics", "15 km EV Mode", "Carbon Monocoque")
                .newDelivery().build());
    }

    static {
        MODEL_LINES.add(new ModelLine("Vantage", "V8 Twin-Turbo", Category.SPORTS,
                "The purest expression of the driver's Aston Martin: compact, front-mid engined and tuned at the Nürburgring.",
                VANTAGE_PODIUM, "₹3.20 Cr", "665 PS", "3.4 s", "325 km/h", "4.0L V8", "NEW GEN"));
        MODEL_LINES.add(new ModelLine("DB12", "Super Tourer", Category.TOURER,
                "The world's first Super Tourer. Grand touring comfort with 680 PS and an all-new handcrafted cabin.",
                DB12_BRONZE, "₹4.45 Cr", "680 PS", "3.6 s", "325 km/h", "4.0L V8", "FLAGSHIP"));
        MODEL_LINES.add(new ModelLine("Vanquish", "V12 Flagship", Category.TOURER,
                "The return of the twelve-cylinder flagship. 835 PS of hand-assembled V12 for the long way home.",
                VANQUISH, "₹8.85 Cr", "835 PS", "3.2 s", "345 km/h", "5.2L V12", "V12"));
        MODEL_LINES.add(new ModelLine("DBX", "Supercar of SUVs", Category.SUV,
                "Five seats, all-wheel drive and 707 PS. Built for every road and every season.",
                DBX707_HIGHLANDS, "₹4.40 Cr", "707 PS", "3.3 s", "310 km/h", "4.0L V8", "AWD"));
        MODEL_LINES.add(new ModelLine("Valhalla", "Hybrid Supercar", Category.SPECIAL,
                "Mid-engined, Formula One derived hybrid hypercar with active aerodynamics and 950 PS.",
                VALHALLA, "On Request", "950 PS", "2.5 s", "350 km/h", "Hybrid V8", "Q BY ASTON MARTIN"));
    }

    private Inventory() {
    }

    public static List<Vehicle> all() {
        return Collections.unmodifiableList(VEHICLES);
    }

    public static List<ModelLine> modelLines() {
        return Collections.unmodifiableList(MODEL_LINES);
    }

    public static Vehicle byId(String id) {
        for (Vehicle v : VEHICLES) {
            if (v.id.equals(id)) return v;
        }
        return null;
    }

    public static List<Vehicle> featured() {
        List<Vehicle> list = new ArrayList<>();
        for (String id : new String[]{"vantage-2024", "db12-bronze", "dbx707-apex", "valhalla"}) {
            list.add(byId(id));
        }
        return list;
    }

    public static List<Vehicle> latestArrivals() {
        List<Vehicle> list = new ArrayList<>();
        list.add(byId("db12-volante"));
        list.add(byId("dbs-770"));
        return list;
    }

    public static int countIn(Category category) {
        int n = 0;
        for (Vehicle v : VEHICLES) {
            if (v.category == category) n++;
        }
        return n;
    }

    public static int countForModel(String modelLine) {
        int n = 0;
        for (Vehicle v : VEHICLES) {
            if (v.modelLine.equals(modelLine)) n++;
        }
        return n;
    }

    /** Category tile imagery for the Showroom bento grid. */
    public static String categoryImage(Category category) {
        switch (category) {
            case TOURER: return DBS_REAR;
            case SPORTS: return VANTAGE_YELLOW;
            case SUV: return DBX_FOREST;
            default: return VALKYRIE;
        }
    }
}
