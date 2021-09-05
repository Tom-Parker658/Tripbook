package com.lado.travago.tripbook.model.enums

/**
 * This file contains text files resources
 * @property regionList contains cameroon places and their respective regions
 * @property journeyDistanceList contains journeys with corresponding distances
 */
object DataResources {
    /**
    val regionPairs = listOf(
    "North" to "North",
    "South" to "South",
    "East" to "East",
    "West" to "West",
    "Littoral" to "Littoral",
    "Centre" to "Centre",
    "SouthWest" to "SouthWest",
    "Adamawa" to "Adamawa",
    "ExtremeNorth" to "ExtremeNorth",
    "NorthWest" to "NorthWest",

    "North" to "South",
    "North" to "East",
    "North" to "Littoral",
    "North" to "West",
    "North" to "NorthWest",
    "North" to "SouthWest",
    "North" to "ExtremeNorth",
    "North" to "Centre",
    "North" to "Adamawa",

    "South" to "North",
    "South" to "East",
    "South" to "Littoral",
    "South" to "West",
    "South" to "NorthWest",
    "South" to "SouthWest",
    "South" to "ExtremeNorth",
    "South" to "Centre",
    "South" to "Adamawa",

    "East" to "North",
    "East" to "South",
    "East" to "Littoral",
    "East" to "West",
    "East" to "NorthWest",
    "East" to "SouthWest",
    "East" to "ExtremeNorth",
    "East" to "Centre",
    "East" to "Adamawa",

    "West" to "South",
    "West" to "East",
    "West" to "Littoral",
    "West" to "North",
    "West" to "NorthWest",
    "West" to "SouthWest",
    "West" to "ExtremeNorth",
    "West" to "Centre",
    "West" to "Adamawa",

    "ExtremeNorth" to "South",
    "ExtremeNorth" to "East",
    "ExtremeNorth" to "Littoral",
    "ExtremeNorth" to "North",
    "ExtremeNorth" to "NorthWest",
    "ExtremeNorth" to "SouthWest",
    "ExtremeNorth" to "West",
    "ExtremeNorth" to "Centre",
    "ExtremeNorth" to "Adamawa",

    "Littoral" to "South",
    "Littoral" to "East",
    "Littoral" to "ExtremeNorth",
    "Littoral" to "North",
    "Littoral" to "NorthWest",
    "Littoral" to "SouthWest",
    "Littoral" to "West",
    "Littoral" to "Centre",
    "Littoral" to "Adamawa",

    "NorthWest" to "South",
    "NorthWest" to "East",
    "NorthWest" to "ExtremeNorth",
    "NorthWest" to "North",
    "NorthWest" to "Littoral",
    "NorthWest" to "SouthWest",
    "NorthWest" to "West",
    "NorthWest" to "Centre",
    "NorthWest" to "Adamawa",

    "SouthWest" to "South",
    "SouthWest" to "East",
    "SouthWest" to "ExtremeNorth",
    "SouthWest" to "North",
    "SouthWest" to "Littoral",
    "SouthWest" to "NorthWest",
    "SouthWest" to "West",
    "SouthWest" to "Centre",
    "SouthWest" to "Adamawa",

    "Adamawa" to "South",
    "Adamawa" to "East",
    "Adamawa" to "ExtremeNorth",
    "Adamawa" to "North",
    "Adamawa" to "Littoral",
    "Adamawa" to "NorthWest",
    "Adamawa" to "West",
    "Adamawa" to "Centre",
    "Adamawa" to "SouthWest",

    "Centre"  to "South",
    "Centre"  to "East",
    "Centre"  to "ExtremeNorth",
    "Centre"  to "North",
    "Centre"  to "Littoral",
    "Centre"  to "NorthWest",
    "Centre"  to "West",
    "Centre"  to "Adamawa",
    "Centre"  to "SouthWest",
    )
     */
    const val regionList = """
        Abong Mbang+East
        Akonolinga+Centre
        Ambam+South
        Bafang+West
        Bafia+Centre
        Bafoussam+West
        Baham+West
        Bamenda+NorthWest
        Bandjoun+West
        Bangangte+West
        Bangem+SouthWest
        Banyo+Adamawa
        Batouri+East
        Bertoua+East
        Buea+SouthWest
        Douala+Littoral
        Dschang+West
        Ebolowa+South
        Edea+Littoral
        Eseka+Centre
        Fontem+SouthWest
        Foumban+West
        Fundong+NorthWest
        Garoua+North
        Guider+ExtremeNorth
        Kaele+ExtremeNorth
        Kousseri+ExtremeNorth
        Kribi+South
        Kumba+SouthWest
        Kumbo+NorthWest
        Limbe+SouthWest
        Mamfe+SouthWest
        Maroua+ExtremeNorth
        Mbalmayo+Centre
        Mbengwi+NorthWest
        Mbouda+West
        Meiganga+Adamawa
        Mfou+Centre
        Mokolo+ExtremeNorth
        Monatele+Littoral
        Mora+ExtremeNorth
        Mundemba+SouthWest
        Nanga Eboko+Centre
        Ndop+NorthWest
        Ngaoundere+Adamawa
        Ngoumou+Centre
        Nkambe+NorthWest
        Nkongsamba+Littoral
        Ntui+Centre
        Poli+North
        Sangmelima+South
        Tchollire+North
        Tibati+Adamawa
        Tignere+Adamawa
        Wum+NorthWest
        Yabassi+Littoral
        Yagoua+ExtremeNorth
        Yaounde+Centre
        Yokadouma+East
    """

    const val journeyDistanceListOriginal = """
    Abong-Mbang	Akonolinga	131	
    Abong-Mbang	Ambam	433	
    Abong-Mbang	Bafang	518	
    Abong-Mbang	Bafia	348	
    Abong-Mbang	Bafoussam	518	
    Abong-Mbang	Baham	511	
    Abong-Mbang	Bamenda	597	
    Abong-Mbang	Bandjoun	504	
    Abong-Mbang	Bangangte	471	
    Abong-Mbang	Bangem	582	
    Abong-Mbang	Banyo	734	
    Abong-Mbang	Batouri	179	
    Abong-Mbang	Bertoua	111	
    Abong-Mbang	Buea	532	
    Abong-Mbang	Douala	467	
    Abong-Mbang	Dschang	565	
    Abong-Mbang	Ebolowa	344	
    Abong-Mbang	Edea	397	
    Abong-Mbang	Eseka	346	
    Abong-Mbang	Fontem	606	
    Abong-Mbang	Foumban	524	
    Abong-Mbang	Fundong	662	
    Abong-Mbang	Garoua	884	
    Abong-Mbang	Guider	987	
    Abong-Mbang	Kaele	1063	
    Abong-Mbang	Kousseri	1349	
    Abong-Mbang	Kribi	498	
    Abong-Mbang	Kumba	598	
    Abong-Mbang	Kumbo	606	
    Abong-Mbang	Limbe	536	
    Abong-Mbang	Mamfe	694	
    Abong-Mbang	Maroua	1085	
    Abong-Mbang	Mbalmayo	254	
    Abong-Mbang	Mbengwi	619	
    Abong-Mbang	Mbouda	546	
    Abong-Mbang	Meiganga	451	
    Abong-Mbang	Mfou	227	
    Abong-Mbang	Mokolo	1092	
    Abong-Mbang	Monatele	306	
    Abong-Mbang	Mora	1145	
    Abong-Mbang	Mundemba	684	
    Abong-Mbang	Nanga-Eboko	201	
    Abong-Mbang	Ndop	591	
    Abong-Mbang	Ngaoundere	607	
    Abong-Mbang	Ngoumou	268	
    Abong-Mbang	Nkambe	674	
    Abong-Mbang	Nkongsamba	571	
    Abong-Mbang	Ntui	284	
    Abong-Mbang	Poli	823	
    Abong-Mbang	Sangmelima	259	
    Abong-Mbang	Tchollire	826	
    Abong-Mbang	Tibati	664	
    Abong-Mbang	Tignere	739	
    Abong-Mbang	Wum	672	
    Abong-Mbang	Yabassi	501	
    Abong-Mbang	Yagoua	1172	
    Abong-Mbang	Yaounde	231	
    Abong-Mbang	Yokadouma	364	
    Akonolinga	Ambam	302	
    Akonolinga	Bafang	403	
    Akonolinga	Bafia	234	
    Akonolinga	Bafoussam	403	
    Akonolinga	Baham	397	
    Akonolinga	Bamenda	483	
    Akonolinga	Bandjoun	390	
    Akonolinga	Bangangte	356	
    Akonolinga	Bangem	467	
    Akonolinga	Banyo	620	
    Akonolinga	Batouri	310	
    Akonolinga	Bertoua	242	
    Akonolinga	Buea	417	
    Akonolinga	Douala	353	
    Akonolinga	Dschang	451	
    Akonolinga	Ebolowa	212	
    Akonolinga	Edea	283	
    Akonolinga	Eseka	231	
    Akonolinga	Fontem	492	
    Akonolinga	Foumban	410	
    Akonolinga	Fundong	547	
    Akonolinga	Garoua	1015	
    Akonolinga	Guider	1119	
    Akonolinga	Kaele	1194	
    Akonolinga	Kousseri	1481	
    Akonolinga	Kribi	484	
    Akonolinga	Kumba	421	
    Akonolinga	Kumbo	491	
    Akonolinga	Limbe	421	
    Akonolinga	Mamfe	579	
    Akonolinga	Maroua	1216	
    Akonolinga	Mbalmayo	123	
    Akonolinga	Mbengwi	505	
    Akonolinga	Mbouda	431	
    Akonolinga	Meiganga	582	
    Akonolinga	Mfou	113	
    Akonolinga	Mokolo	1223	
    Akonolinga	Monatele	191	
    Akonolinga	Mora	1277	
    Akonolinga	Mundemba	570	
    Akonolinga	Nanga-Eboko	138	
    Akonolinga	Ndop	476	
    Akonolinga	Ngaoundere	739	
    Akonolinga	Ngoumou	154	
    Akonolinga	Nkambe	560	
    Akonolinga	Nkongsamba	457	
    Akonolinga	Ntui	170	
    Akonolinga	Poli	954	
    Akonolinga	Sangmelima	129	
    Akonolinga	Tchollire	957	
    Akonolinga	Tibati	550	
    Akonolinga	Tignere	702	
    Akonolinga	Wum	558	
    Akonolinga	Yabassi	387	
    Akonolinga	Yagoua	1303	
    Akonolinga	Yaounde	117	
    Akonolinga	Yokadouma	496	
    Ambam	Bafang	518	
    Ambam	Bafia	366	
    Ambam	Bafoussam	518	
    Ambam	Baham	511	
    Ambam	Bamenda	597	
    Ambam	Bandjoun	504	
    Ambam	Bangangte	471	
    Ambam	Bangem	542	
    Ambam	Banyo	752	
    Ambam	Batouri	612	
    Ambam	Bertoua	544	
    Ambam	Buea	455	
    Ambam	Douala	390	
    Ambam	Dschang	565	
    Ambam	Ebolowa	89	
    Ambam	Edea	320	
    Ambam	Eseka	222	
    Ambam	Fontem	606	
    Ambam	Foumban	542	
    Ambam	Fundong	662	
    Ambam	Garoua	1197	
    Ambam	Guider	1301	
    Ambam	Kaele	1376	
    Ambam	Kousseri	1663	
    Ambam	Kribi	255	
    Ambam	Kumba	521	
    Ambam	Kumbo	624	
    Ambam	Limbe	459	
    Ambam	Mamfe	669	
    Ambam	Maroua	1398	
    Ambam	Mbalmayo	194	
    Ambam	Mbengwi	619	
    Ambam	Mbouda	546	
    Ambam	Meiganga	884	
    Ambam	Mfou	239	
    Ambam	Mokolo	1405	
    Ambam	Monatele	317	
    Ambam	Mora	1459	
    Ambam	Mundemba	607	
    Ambam	Nanga-Eboko	405	
    Ambam	Ndop	605	
    Ambam	Ngaoundere	923	
    Ambam	Ngoumou	231	
    Ambam	Nkambe	692	
    Ambam	Nkongsamba	520	
    Ambam	Ntui	322	
    Ambam	Poli	1136	
    Ambam	Sangmelima	208	
    Ambam	Tchollire	1139	
    Ambam	Tibati	702	
    Ambam	Tignere	854	
    Ambam	Wum	672	
    Ambam	Yabassi	424	
    Ambam	Yagoua	1485	
    Ambam	Yaounde	241	
    Ambam	Yokadouma	797	
    Bafang	Bafia	169	
    Bafang	Bafoussam	60	
    Bafang	Baham	39	
    Bafang	Bamenda	124	
    Bafang	Bandjoun	46	
    Bafang	Bangangte	47	
    Bafang	Bangem	65	
    Bafang	Banyo	339	
    Bafang	Batouri	636	
    Bafang	Bertoua	547	
    Bafang	Buea	206	
    Bafang	Douala	195	
    Bafang	Dschang	69	
    Bafang	Ebolowa	428	
    Bafang	Edea	250	
    Bafang	Eseka	296	
    Bafang	Fontem	111	
    Bafang	Foumban	128	
    Bafang	Fundong	188	
    Bafang	Garoua	943	
    Bafang	Guider	1047	
    Bafang	Kaele	1123	
    Bafang	Kousseri	1409	
    Bafang	Kribi	361	
    Bafang	Kumba	135	
    Bafang	Kumbo	200	
    Bafang	Limbe	225	
    Bafang	Mamfe	198	
    Bafang	Maroua	1144	
    Bafang	Mbalmayo	341	
    Bafang	Mbengwi	146	
    Bafang	Mbouda	72	
    Bafang	Meiganga	700	
    Bafang	Mfou	323	
    Bafang	Mokolo	1152	
    Bafang	Monatele	236	
    Bafang	Mora	1205	
    Bafang	Mundemba	247	
    Bafang	Nanga-Eboko	373	
    Bafang	Ndop	132	
    Bafang	Ngaoundere	669	
    Bafang	Ngoumou	343	
    Bafang	Nkambe	268	
    Bafang	Nkongsamba	55	
    Bafang	Ntui	248	
    Bafang	Poli	883	
    Bafang	Sangmelima	453	
    Bafang	Tchollire	883	
    Bafang	Tibati	449	
    Bafang	Tignere	600	
    Bafang	Wum	199	
    Bafang	Yabassi	146	
    Bafang	Yagoua	1231	
    Bafang	Yaounde	294	
    Bafang	Yokadouma	821	
    Bafia	Bafoussam	170	
    Bafia	Baham	163	
    Bafia	Bamenda	249	
    Bafia	Bandjoun	156	
    Bafia	Bangangte	122	
    Bafia	Bangem	233	
    Bafia	Banyo	386	
    Bafia	Batouri	474	
    Bafia	Bertoua	385	
    Bafia	Buea	340	
    Bafia	Douala	276	
    Bafia	Dschang	217	
    Bafia	Ebolowa	277	
    Bafia	Edea	212	
    Bafia	Eseka	160	
    Bafia	Fontem	258	
    Bafia	Foumban	176	
    Bafia	Fundong	314	
    Bafia	Garoua	898	
    Bafia	Guider	1002	
    Bafia	Kaele	1078	
    Bafia	Kousseri	1364	
    Bafia	Kribi	322	
    Bafia	Kumba	303	
    Bafia	Kumbo	258	
    Bafia	Limbe	344	
    Bafia	Mamfe	346	
    Bafia	Maroua	1099	
    Bafia	Mbalmayo	172	
    Bafia	Mbengwi	271	
    Bafia	Mbouda	197	
    Bafia	Meiganga	655	
    Bafia	Mfou	154	
    Bafia	Mokolo	1107	
    Bafia	Monatele	67	
    Bafia	Mora	1160	
    Bafia	Mundemba	416	
    Bafia	Nanga-Eboko	211	
    Bafia	Ndop	242	
    Bafia	Ngaoundere	624	
    Bafia	Ngoumou	173	
    Bafia	Nkambe	326	
    Bafia	Nkongsamba	223	
    Bafia	Ntui	85	
    Bafia	Poli	838	
    Bafia	Sangmelima	284	
    Bafia	Tchollire	840	
    Bafia	Tibati	403	
    Bafia	Tignere	555	
    Bafia	Wum	324	
    Bafia	Yabassi	222	
    Bafia	Yagoua	1186	
    Bafia	Yaounde	125	
    Bafia	Yokadouma	659	
    Bafoussam	Baham	21	
    Bafoussam	Bamenda	79	
    Bafoussam	Bandjoun	14	
    Bafoussam	Bangangte	47	
    Bafoussam	Bangem	124	
    Bafoussam	Banyo	280	
    Bafoussam	Batouri	636	
    Bafoussam	Bertoua	547	
    Bafoussam	Buea	265	
    Bafoussam	Douala	253	
    Bafoussam	Dschang	50	
    Bafoussam	Ebolowa	429	
    Bafoussam	Edea	300	
    Bafoussam	Eseka	296	
    Bafoussam	Fontem	91	
    Bafoussam	Foumban	68	
    Bafoussam	Fundong	144	
    Bafoussam	Garoua	884	
    Bafoussam	Guider	987	
    Bafoussam	Kaele	1063	
    Bafoussam	Kousseri	1349	
    Bafoussam	Kribi	411	
    Bafoussam	Kumba	193	
    Bafoussam	Kumbo	143	
    Bafoussam	Limbe	284	
    Bafoussam	Mamfe	179	
    Bafoussam	Maroua	1085	
    Bafoussam	Mbalmayo	342	
    Bafoussam	Mbengwi	101	
    Bafoussam	Mbouda	28	
    Bafoussam	Meiganga	641	
    Bafoussam	Mfou	324	
    Bafoussam	Mokolo	1092	
    Bafoussam	Monatele	236	
    Bafoussam	Mora	1145	
    Bafoussam	Mundemba	277	
    Bafoussam	Nanga-Eboko	373	
    Bafoussam	Ndop	87	
    Bafoussam	Ngaoundere	610	
    Bafoussam	Ngoumou	343	
    Bafoussam	Nkambe	212	
    Bafoussam	Nkongsamba	113	
    Bafoussam	Ntui	248	
    Bafoussam	Poli	823	
    Bafoussam	Sangmelima	453	
    Bafoussam	Tchollire	826	
    Bafoussam	Tibati	389	
    Bafoussam	Tignere	541	
    Bafoussam	Wum	154	
    Bafoussam	Yabassi	205	
    Bafoussam	Yagoua	1172	
    Bafoussam	Yaounde	295	
    Bafoussam	Yokadouma	822	
    Baham	Bamenda	100	
    Baham	Bandjoun	7	
    Baham	Bangangte	41	
    Baham	Bangem	103	
    Baham	Banyo	301	
    Baham	Batouri	629	
    Baham	Bertoua	540	
    Baham	Buea	244	
    Baham	Douala	233	
    Baham	Dschang	59	
    Baham	Ebolowa	422	
    Baham	Edea	288	
    Baham	Eseka	289	
    Baham	Fontem	100	
    Baham	Foumban	89	
    Baham	Fundong	165	
    Baham	Garoua	904	
    Baham	Guider	1008	
    Baham	Kaele	1084	
    Baham	Kousseri	1370	
    Baham	Kribi	399	
    Baham	Kumba	173	
    Baham	Kumbo	164	
    Baham	Limbe	263	
    Baham	Mamfe	188	
    Baham	Maroua	1105	
    Baham	Mbalmayo	335	
    Baham	Mbengwi	122	
    Baham	Mbouda	48	
    Baham	Meiganga	662	
    Baham	Mfou	317	
    Baham	Mokolo	1113	
    Baham	Monatele	229	
    Baham	Mora	1166	
    Baham	Mundemba	285	
    Baham	Nanga-Eboko	366	
    Baham	Ndop	108	
    Baham	Ngaoundere	631	
    Baham	Ngoumou	336	
    Baham	Nkambe	232	
    Baham	Nkongsamba	92	
    Baham	Ntui	241	
    Baham	Poli	844	
    Baham	Sangmelima	447	
    Baham	Tchollire	847	
    Baham	Tibati	410	
    Baham	Tignere	561	
    Baham	Wum	175	
    Baham	Yabassi	184	
    Baham	Yagoua	1193	
    Baham	Yaounde	288	
    Baham	Yokadouma	815	
    Bamenda	Bandjoun	93	
    Bamenda	Bangangte	127	
    Bamenda	Bangem	172	
    Bamenda	Banyo	356	
    Bamenda	Batouri	716	
    Bamenda	Bertoua	627	
    Bamenda	Buea	313	
    Bamenda	Douala	302	
    Bamenda	Dschang	89	
    Bamenda	Ebolowa	508	
    Bamenda	Edea	361	
    Bamenda	Eseka	375	
    Bamenda	Fontem	130	
    Bamenda	Foumban	145	
    Bamenda	Fundong	71	
    Bamenda	Garoua	960	
    Bamenda	Guider	1064	
    Bamenda	Kaele	1140	
    Bamenda	Kousseri	1426	
    Bamenda	Kribi	472	
    Bamenda	Kumba	242	
    Bamenda	Kumbo	103	
    Bamenda	Limbe	332	
    Bamenda	Mamfe	139	
    Bamenda	Maroua	1161	
    Bamenda	Mbalmayo	421	
    Bamenda	Mbengwi	22	
    Bamenda	Mbouda	52	
    Bamenda	Meiganga	717	
    Bamenda	Mfou	403	
    Bamenda	Mokolo	1169	
    Bamenda	Monatele	316	
    Bamenda	Mora	1222	
    Bamenda	Mundemba	285	
    Bamenda	Nanga-Eboko	453	
    Bamenda	Ndop	38	
    Bamenda	Ngaoundere	686	
    Bamenda	Ngoumou	423	
    Bamenda	Nkambe	171	
    Bamenda	Nkongsamba	161	
    Bamenda	Ntui	327	
    Bamenda	Poli	900	
    Bamenda	Sangmelima	533	
    Bamenda	Tchollire	903	
    Bamenda	Tibati	466	
    Bamenda	Tignere	617	
    Bamenda	Wum	75	
    Bamenda	Yabassi	258	
    Bamenda	Yagoua	1248	
    Bamenda	Yaounde	374	
    Bamenda	Yokadouma	901	
    Bandjoun	Bangangte	34	
    Bandjoun	Bangem	110	
    Bandjoun	Banyo	294	
    Bandjoun	Batouri	622	
    Bandjoun	Bertoua	533	
    Bandjoun	Buea	251	
    Bandjoun	Douala	240	
    Bandjoun	Dschang	61	
    Bandjoun	Ebolowa	415	
    Bandjoun	Edea	287	
    Bandjoun	Eseka	282	
    Bandjoun	Fontem	102	
    Bandjoun	Foumban	82	
    Bandjoun	Fundong	158	
    Bandjoun	Garoua	897	
    Bandjoun	Guider	1001	
    Bandjoun	Kaele	1077	
    Bandjoun	Kousseri	1363	
    Bandjoun	Kribi	397	
    Bandjoun	Kumba	180	
    Bandjoun	Kumbo	157	
    Bandjoun	Limbe	270	
    Bandjoun	Mamfe	190	
    Bandjoun	Maroua	1098	
    Bandjoun	Mbalmayo	328	
    Bandjoun	Mbengwi	115	
    Bandjoun	Mbouda	41	
    Bandjoun	Meiganga	655	
    Bandjoun	Mfou	310	
    Bandjoun	Mokolo	1106	
    Bandjoun	Monatele	222	
    Bandjoun	Mora	1159	
    Bandjoun	Mundemba	289	
    Bandjoun	Nanga-Eboko	359	
    Bandjoun	Ndop	101	
    Bandjoun	Ngaoundere	624	
    Bandjoun	Ngoumou	329	
    Bandjoun	Nkambe	225	
    Bandjoun	Nkongsamba	99	
    Bandjoun	Ntui	234	
    Bandjoun	Poli	837	
    Bandjoun	Sangmelima	440	
    Bandjoun	Tchollire	840	
    Bandjoun	Tibati	403	
    Bandjoun	Tignere	554	
    Bandjoun	Wum	168	
    Bandjoun	Yabassi	191	
    Bandjoun	Yagoua	1186	
    Bandjoun	Yaounde	281	
    Bandjoun	Yokadouma	808	
    Bangangte	Bangem	111	
    Bangangte	Banyo	304	
    Bangangte	Batouri	589	
    Bangangte	Bertoua	500	
    Bangangte	Buea	252	
    Bangangte	Douala	241	
    Bangangte	Dschang	95	
    Bangangte	Ebolowa	381	
    Bangangte	Edea	253	
    Bangangte	Eseka	248	
    Bangangte	Fontem	136	
    Bangangte	Foumban	93	
    Bangangte	Fundong	191	
    Bangangte	Garoua	908	
    Bangangte	Guider	1012	
    Bangangte	Kaele	1088	
    Bangangte	Kousseri	1374	
    Bangangte	Kribi	364	
    Bangangte	Kumba	181	
    Bangangte	Kumbo	168	
    Bangangte	Limbe	271	
    Bangangte	Mamfe	223	
    Bangangte	Maroua	1109	
    Bangangte	Mbalmayo	294	
    Bangangte	Mbengwi	149	
    Bangangte	Mbouda	75	
    Bangangte	Meiganga	666	
    Bangangte	Mfou	276	
    Bangangte	Mokolo	1117	
    Bangangte	Monatele	189	
    Bangangte	Mora	1170	
    Bangangte	Mundemba	293	
    Bangangte	Nanga-Eboko	326	
    Bangangte	Ndop	135	
    Bangangte	Ngaoundere	635	
    Bangangte	Ngoumou	296	
    Bangangte	Nkambe	236	
    Bangangte	Nkambe	236	
    Bangangte	Nkongsamba	101	
    Bangangte	Ntui	200	
    Bangangte	Poli	848	
    Bangangte	Sangmelima	406	
    Bangangte	Tchollire	851	
    Bangangte	Tibati	414	
    Bangangte	Tignere	565	
    Bangangte	Wum	202	
    Bangangte	Yabassi	193	
    Bangangte	Yagoua	1197	
    Bangangte	Yaounde	247	
    Bangangte	Yokadouma	774	
    Bangem	Banyo	403	
    Bangem	Batouri	700	
    Bangem	Bertoua	611	
    Bangem	Buea	163	
    Bangem	Douala	162	
    Bangem	Dschang	83	
    Bangem	Ebolowa	453	
    Bangem	Edea	222	
    Bangem	Eseka	329	
    Bangem	Fontem	124	
    Bangem	Foumban	191	
    Bangem	Fundong	236	
    Bangem	Garoua	1007	
    Bangem	Guider	1111	
    Bangem	Kaele	1187	
    Bangem	Kousseri	1473	
    Bangem	Kribi	332	
    Bangem	Kumba	91	
    Bangem	Kumbo	248	
    Bangem	Limbe	182	
    Bangem	Mamfe	211	
    Bangem	Maroua	1208	
    Bangem	Mbalmayo	405	
    Bangem	Mbengwi	194	
    Bangem	Mbouda	120	
    Bangem	Meiganga	764	
    Bangem	Mfou	387	
    Bangem	Mokolo	1216	
    Bangem	Monatele	300	
    Bangem	Mora	1269	
    Bangem	Mundemba	204	
    Bangem	Nanga-Eboko	437	
    Bangem	Ndop	179	
    Bangem	Ngaoundere	733	
    Bangem	Ngoumou	407	
    Bangem	Nkambe	316	
    Bangem	Nkongsamba	55	
    Bangem	Ntui	311	
    Bangem	Poli	947	
    Bangem	Sangmelima	517	
    Bangem	Tchollire	950	
    Bangem	Tibati	513	
    Bangem	Tignere	664	
    Bangem	Wum	246	
    Bangem	Yabassi	118	
    Bangem	Yagoua	1295	
    Bangem	Yaounde	358	
    Bangem	Yokadouma	885	
    Banyo	Batouri	671	
    Banyo	Bertoua	682	
    Banyo	Buea	544	
    Banyo	Douala	533	
    Banyo	Dschang	330	
    Banyo	Ebolowa	663	
    Banyo	Edea	558	
    Banyo	Eseka	546	
    Banyo	Fontem	371	
    Banyo	Foumban	212	
    Banyo	Fundong	333	
    Banyo	Garoua	604	
    Banyo	Guider	708	
    Banyo	Kaele	783	
    Banyo	Kousseri	1070	
    Banyo	Kribi	668	
    Banyo	Kumba	473	
    Banyo	Kumbo	253	
    Banyo	Limbe	563	
    Banyo	Mamfe	458	
    Banyo	Maroua	805	
    Banyo	Mbalmayo	558	
    Banyo	Mbengwi	378	
    Banyo	Mbouda	306	
    Banyo	Meiganga	361	
    Banyo	Mfou	540	
    Banyo	Mokolo	812	
    Banyo	Monatele	453	
    Banyo	Mora	866	
    Banyo	Mundemba	557	
    Banyo	Nanga-Eboko	590	
    Banyo	Ndop	321	
    Banyo	Ngaoundere	330	
    Banyo	Ngoumou	560	
    Banyo	Nkambe	265	
    Banyo	Nkongsamba	393	
    Banyo	Ntui	464	
    Banyo	Poli	543	
    Banyo	Sangmelima	670	
    Banyo	Tchollire	546	
    Banyo	Tibati	110	
    Banyo	Tignere	261	
    Banyo	Wum	374	
    Banyo	Yabassi	485	
    Banyo	Yagoua	892	
    Banyo	Yaounde	511	
    Banyo	Yokadouma	852	
    Batouri	Bertoua	89	
    Batouri	Buea	711	
    Batouri	Douala	646	
    Batouri	Dschang	683	
    Batouri	Ebolowa	523	
    Batouri	Edea	576	
    Batouri	Eseka	525	
    Batouri	Fontem	725	
    Batouri	Foumban	643	
    Batouri	Fundong	780	
    Batouri	Garoua	762	
    Batouri	Guider	865	
    Batouri	Kaele	941	
    Batouri	Kousseri	1227	
    Batouri	Kribi	677	
    Batouri	Kumba	770	
    Batouri	Kumbo	724	
    Batouri	Limbe	715	
    Batouri	Mamfe	812	
    Batouri	Maroua	963	
    Batouri	Mbalmayo	433	
    Batouri	Mbengwi	738	
    Batouri	Mbouda	664	
    Batouri	Meiganga	329	
    Batouri	Mfou	406	
    Batouri	Mokolo	970	
    Batouri	Monatele	436	
    Batouri	Mora	1024	
    Batouri	Mundemba	863	
    Batouri	Nanga-Eboko	263	
    Batouri	Ndop	709	
    Batouri	Ngaoundere	486	
    Batouri	Ngoumou	447	
    Batouri	Nkambe	793	
    Batouri	Nkongsamba	690	
    Batouri	Ntui	388	
    Batouri	Poli	701	
    Batouri	Sangmelima	438	
    Batouri	Tchollire	704	
    Batouri	Tibati	561	
    Batouri	Tignere	618	
    Batouri	Wum	790	
    Batouri	Yabassi	680	
    Batouri	Yagoua	1050	
    Batouri	Yaounde	410	
    Batouri	Yokadouma	185	
    Bertoua	Buea	642	
    Bertoua	Douala	578	
    Bertoua	Dschang	594	
    Bertoua	Ebolowa	454	
    Bertoua	Edea	508	
    Bertoua	Eseka	457	
    Bertoua	Fontem	636	
    Bertoua	Foumban	554	
    Bertoua	Fundong	691	
    Bertoua	Garoua	773	
    Bertoua	Guider	876	
    Bertoua	Kaele	952	
    Bertoua	Kousseri	1238	
    Bertoua	Kribi	608	
    Bertoua	Kumba	681	
    Bertoua	Kumbo	635	
    Bertoua	Limbe	646	
    Bertoua	Mamfe	723	
    Bertoua	Maroua	974	
    Bertoua	Mbalmayo	365	
    Bertoua	Mbengwi	649	
    Bertoua	Mbouda	575	
    Bertoua	Meiganga	340	
    Bertoua	Mfou	338	
    Bertoua	Mokolo	981	
    Bertoua	Monatele	347	
    Bertoua	Mora	1034	
    Bertoua	Mundemba	793	
    Bertoua	Nanga-Eboko	174	
    Bertoua	Ndop	620	
    Bertoua	Ngaoundere	496	
    Bertoua	Ngoumou	379	
    Bertoua	Nkambe	704	
    Bertoua	Nkongsamba	601	
    Bertoua	Ntui	299	
    Bertoua	Poli	712	
    Bertoua	Sangmelima	369	
    Bertoua	Tchollire	715	
    Bertoua	Tibati	572	
    Bertoua	Tignere	629	
    Bertoua	Wum	701	
    Bertoua	Yabassi	599	
    Bertoua	Yagoua	1061	
    Bertoua	Yaounde	337	
    Bertoua	Yokadouma	274	
    Buea	Douala	64	
    Buea	Dschang	224	
    Buea	Ebolowa	365	
    Buea	Edea	134	
    Buea	Eseka	242	
    Buea	Fontem	249	
    Buea	Foumban	333	
    Buea	Fundong	377	
    Buea	Garoua	1148	
    Buea	Guider	1252	
    Buea	Kaele	1328	
    Buea	Kousseri	1614	
    Buea	Kribi	245	
    Buea	Kumba	71	
    Buea	Kumbo	389	
    Buea	Limbe	22	
    Buea	Mamfe	230	
    Buea	Maroua	1349	
    Buea	Mbalmayo	346	
    Buea	Mbengwi	335	
    Buea	Mbouda	261	
    Buea	Meiganga	905	
    Buea	Mfou	330	
    Buea	Mokolo	1357	
    Buea	Monatele	319	
    Buea	Mora	1410	
    Buea	Mundemba	157	
    Buea	Nanga-Eboko	469	
    Buea	Ndop	321	
    Buea	Ngaoundere	874	
    Buea	Ngoumou	324	
    Buea	Nkambe	457	
    Buea	Nkongsamba	151	
    Buea	Ntui	386	
    Buea	Poli	1088	
    Buea	Sangmelima	459	
    Buea	Tchollire	1091	
    Buea	Tibati	654	
    Buea	Tignere	805	
    Buea	Wum	388	
    Buea	Yabassi	163	
    Buea	Yagoua	1438	
    Buea	Yaounde	307	
    Buea	Yokadouma	896	
    Douala	Dschang	213	
    Douala	Ebolowa	301	
    Douala	Edea	70	
    Douala	Eseka	178	
    Douala	Fontem	254	
    Douala	Foumban	321	
    Douala	Fundong	366	
    Douala	Garoua	1137	
    Douala	Guider	1241	
    Douala	Kaele	1317	
    Douala	Kousseri	1603	
    Douala	Kribi	180	
    Douala	Kumba	131	
    Douala	Kumbo	378	
    Douala	Limbe	68	
    Douala	Mamfe	289	
    Douala	Maroua	1338	
    Douala	Mbalmayo	281	
    Douala	Mbengwi	324	
    Douala	Mbouda	250	
    Douala	Meiganga	894	
    Douala	Mfou	265	
    Douala	Mokolo	1346	
    Douala	Monatele	254	
    Douala	Mora	1399	
    Douala	Mundemba	217	
    Douala	Nanga-Eboko	404	
    Douala	Ndop	309	
    Douala	Ngaoundere	863	
    Douala	Ngoumou	260	
    Douala	Nkambe	446	
    Douala	Nkongsamba	140	
    Douala	Ntui	322	
    Douala	Poli	1077	
    Douala	Sangmelima	395	
    Douala	Tchollire	1080	
    Douala	Tibati	643	
    Douala	Tignere	794	
    Douala	Wum	376	
    Douala	Yabassi	98	
    Douala	Yagoua	1425	
    Douala	Yaounde	243	
    Douala	Yokadouma	832	
    Dschang	Ebolowa	476	
    Dschang	Edea	272	
    Dschang	Eseka	343	
    Dschang	Fontem	41	
    Dschang	Foumban	118	
    Dschang	Fundong	154	
    Dschang	Garoua	933	
    Dschang	Guider	1037	
    Dschang	Kaele	1113	
    Dschang	Kousseri	1399	
    Dschang	Kribi	383	
    Dschang	Kumba	153	
    Dschang	Kumbo	165	
    Dschang	Limbe	243	
    Dschang	Mamfe	129	
    Dschang	Maroua	1134	
    Dschang	Mbalmayo	389	
    Dschang	Mbengwi	111	
    Dschang	Mbouda	37	
    Dschang	Meiganga	691	
    Dschang	Mfou	371	
    Dschang	Mokolo	1142	
    Dschang	Monatele	283	
    Dschang	Mora	1195	
    Dschang	Mundemba	227	
    Dschang	Nanga-Eboko	420	
    Dschang	Ndop	97	
    Dschang	Ngaoundere	660	
    Dschang	Ngoumou	390	
    Dschang	Nkambe	233	
    Dschang	Nkongsamba	73	
    Dschang	Ntui	295	
    Dschang	Poli	873	
    Dschang	Sangmelima	501	
    Dschang	Tchollire	876	
    Dschang	Tibati	439	
    Dschang	Tignere	590	
    Dschang	Wum	164	
    Dschang	Yabassi	169	
    Dschang	Yagoua	1222	
    Dschang	Yaounde	342	
    Dschang	Yokadouma	869	
    Ebolowa	Edea	231	
    Ebolowa	Eseka	133	
    Ebolowa	Fontem	517	
    Ebolowa	Foumban	453	
    Ebolowa	Fundong	573	
    Ebolowa	Garoua	1108	
    Ebolowa	Guider	1211	
    Ebolowa	Kaele	1287	
    Ebolowa	Kousseri	1573	
    Ebolowa	Kribi	172	
    Ebolowa	Kumba	432	
    Ebolowa	Kumbo	535	
    Ebolowa	Limbe	369	
    Ebolowa	Mamfe	580	
    Ebolowa	Maroua	1308	
    Ebolowa	Mbalmayo	105	
    Ebolowa	Mbengwi	530	
    Ebolowa	Mbouda	456	
    Ebolowa	Meiganga	795	
    Ebolowa	Mfou	149	
    Ebolowa	Mokolo	1316	
    Ebolowa	Monatele	228	
    Ebolowa	Mora	1369	
    Ebolowa	Mundemba	518	
    Ebolowa	Nanga-Eboko	315	
    Ebolowa	Ndop	516	
    Ebolowa	Ngaoundere	834	
    Ebolowa	Ngoumou	142	
    Ebolowa	Nkambe	603	
    Ebolowa	Nkongsamba	431	
    Ebolowa	Ntui	233	
    Ebolowa	Poli	1047	
    Ebolowa	Sangmelima	119	
    Ebolowa	Tchollire	1050	
    Ebolowa	Tibati	613	
    Ebolowa	Tignere	765	
    Ebolowa	Wum	583	
    Ebolowa	Yabassi	335	
    Ebolowa	Yagoua	1396	
    Ebolowa	Yaounde	152	
    Ebolowa	Yokadouma	708	
    Edea	Eseka	108	
    Edea	Fontem	314	
    Edea	Foumban	346	
    Edea	Fundong	426	
    Edea	Garoua	1110	
    Edea	Guider	1213	
    Edea	Kaele	1289	
    Edea	Kousseri	1575	
    Edea	Kribi	111	
    Edea	Kumba	201	
    Edea	Kumbo	421	
    Edea	Limbe	138	
    Edea	Mamfe	348	
    Edea	Maroua	1311	
    Edea	Mbalmayo	212	
    Edea	Mbengwi	383	
    Edea	Mbouda	310	
    Edea	Meiganga	849	
    Edea	Mfou	195	
    Edea	Mokolo	1318	
    Edea	Monatele	185	
    Edea	Mora	1371	
    Edea	Mundemba	287	
    Edea	Nanga-Eboko	335	
    Edea	Ndop	369	
    Edea	Ngaoundere	836	
    Edea	Ngoumou	190	
    Edea	Nkambe	489	
    Edea	Nkongsamba	200	
    Edea	Ntui	252	
    Edea	Poli	1049	
    Edea	Sangmelima	325	
    Edea	Tchollire	1052	
    Edea	Tibati	615	
    Edea	Tignere	767	
    Edea	Wum	436	
    Edea	Yabassi	104	
    Edea	Yagoua	1398	
    Edea	Yaounde	173	
    Edea	Yokadouma	762	
    Eseka	Fontem	384	
    Eseka	Foumban	336	
    Eseka	Fundong	440	
    Eseka	Garoua	1058	
    Eseka	Guider	1162	
    Eseka	Kaele	1237	
    Eseka	Kousseri	1524	
    Eseka	Kribi	169	
    Eseka	Kumba	308	
    Eseka	Kumbo	416	
    Eseka	Limbe	246	
    Eseka	Mamfe	456	
    Eseka	Maroua	1259	
    Eseka	Mbalmayo	121	
    Eseka	Mbengwi	397	
    Eseka	Mbouda	323	
    Eseka	Meiganga	797	
    Eseka	Mfou	144	
    Eseka	Mokolo	1266	
    Eseka	Monatele	133	
    Eseka	Mora	1320	
    Eseka	Mundemba	394	
    Eseka	Nanga-Eboko	283	
    Eseka	Ndop	383	
    Eseka	Ngaoundere	784	
    Eseka	Ngoumou	83	
    Eseka	Nkambe	485	
    Eseka	Nkongsamba	308	
    Eseka	Ntui	201	
    Eseka	Poli	997	
    Eseka	Sangmelima	242	
    Eseka	Tchollire	1000	
    Eseka	Tibati	563	
    Eseka	Tignere	715	
    Eseka	Wum	450	
    Eseka	Yabassi	211	
    Eseka	Yagoua	1346	
    Eseka	Yaounde	121	
    Eseka	Yokadouma	710	
    Fontem	Foumban	159	
    Fontem	Fundong	195	
    Fontem	Garoua	975	
    Fontem	Guider	1078	
    Fontem	Kaele	1154	
    Fontem	Kousseri	1440	
    Fontem	Kribi	424	
    Fontem	Kumba	178	
    Fontem	Kumbo	206	
    Fontem	Limbe	269	
    Fontem	Mamfe	88	
    Fontem	Maroua	1176	
    Fontem	Mbalmayo	430	
    Fontem	Mbengwi	152	
    Fontem	Mbouda	78	
    Fontem	Meiganga	732	
    Fontem	Mfou	412	
    Fontem	Mokolo	1183	
    Fontem	Monatele	325	
    Fontem	Mora	1236	
    Fontem	Mundemba	186	
    Fontem	Nanga-Eboko	462	
    Fontem	Ndop	138	
    Fontem	Ngaoundere	701	
    Fontem	Ngoumou	432	
    Fontem	Nkambe	274	
    Fontem	Nkongsamba	114	
    Fontem	Ntui	336	
    Fontem	Poli	914	
    Fontem	Sangmelima	542	
    Fontem	Tchollire	917	
    Fontem	Tibati	480	
    Fontem	Tignere	632	
    Fontem	Wum	205	
    Fontem	Yabassi	210	
    Fontem	Yagoua	1263	
    Fontem	Yokadouma	910	
    Fontem	Yaounde	383	
    Foumban	Fundong	168	
    Foumban	Garoua	816	
    Foumban	Guider	920	
    Foumban	Kaele	995	
    Foumban	Kousseri	1282	
    Foumban	Kribi	456	
    Foumban	Kumba	261	
    Foumban	Kumbo	88	
    Foumban	Limbe	352	
    Foumban	Mamfe	247	
    Foumban	Maroua	1017	
    Foumban	Mbalmayo	348	
    Foumban	Mbengwi	168	
    Foumban	Mbouda	95	
    Foumban	Meiganga	573	
    Foumban	Mfou	330	
    Foumban	Mokolo	1024	
    Foumban	Monatele	243	
    Foumban	Mora	1078	
    Foumban	Mundemba	345	
    Foumban	Nanga-Eboko	380	
    Foumban	Ndop	115	
    Foumban	Ngaoundere	542	
    Foumban	Ngoumou	350	
    Foumban	Nkambe	157	
    Foumban	Nkongsamba	181	
    Foumban	Ntui	254	
    Foumban	Poli	755	
    Foumban	Sangmelima	460	
    Foumban	Tchollire	758	
    Foumban	Tibati	321	
    Foumban	Tignere	473	
    Foumban	Wum	211	
    Foumban	Yabassi	273	
    Foumban	Yagoua	1104	
    Foumban	Yaounde	301	
    Foumban	Yokadouma	828	
    Fundong	Garoua	937	
    Fundong	Guider	1040	
    Fundong	Kaele	1116	
    Fundong	Kousseri	1402	
    Fundong	Kribi	537	
    Fundong	Kumba	306	
    Fundong	Kumbo	79	
    Fundong	Limbe	396	
    Fundong	Mamfe	211	
    Fundong	Maroua	1137	
    Fundong	Mbalmayo	486	
    Fundong	Mbengwi	93	
    Fundong	Mbouda	116	
    Fundong	Meiganga	694	
    Fundong	Mfou	468	
    Fundong	Mokolo	1145	
    Fundong	Monatele	380	
    Fundong	Mora	1198	
    Fundong	Mundemba	356	
    Fundong	Nanga-Eboko	517	
    Fundong	Ndop	81	
    Fundong	Ngaoundere	663	
    Fundong	Ngoumou	487	
    Fundong	Nkambe	132	
    Fundong	Nkongsamba	226	
    Fundong	Ntui	392	
    Fundong	Poli	876	
    Fundong	Sangmelima	597	
    Fundong	Tchollire	879	
    Fundong	Tibati	442	
    Fundong	Tignere	594	
    Fundong	Wum	47	
    Fundong	Yabassi	322	
    Fundong	Yagoua	1225	
    Fundong	Yaounde	439	
    Fundong	Yokadouma	966	
    Garoua	Guider	104	
    Garoua	Kaele	179	
    Garoua	Kousseri	466	
    Garoua	Kribi	1220	
    Garoua	Kumba	1077	
    Garoua	Kumbo	857	
    Garoua	Limbe	1167	
    Garoua	Mamfe	1062	
    Garoua	Maroua	201	
    Garoua	Mbalmayo	1003	
    Garoua	Mbengwi	982	
    Garoua	Mbouda	910	
    Garoua	Meiganga	432	
    Garoua	Mfou	985	
    Garoua	Mokolo	208	
    Garoua	Monatele	950	
    Garoua	Mora	262	
    Garoua	Mundemba	1161	
    Garoua	Nanga-Eboko	947	
    Garoua	Ndop	925	
    Garoua	Ngaoundere	276	
    Garoua	Ngoumou	1004	
    Garoua	Nkambe	869	
    Garoua	Nkongsamba	997	
    Garoua	Ntui	875	
    Garoua	Poli	137	
    Garoua	Sangmelima	1114	
    Garoua	Tchollire	195	
    Garoua	Tibati	495	
    Garoua	Tignere	406	
    Garoua	Wum	978	
    Garoua	Yabassi	1089	
    Garoua	Yagoua	288	
    Garoua	Yaounde	956	
    Garoua	Yokadouma	943	
    Guider	Kaele	80	
    Guider	Kousseri	362	
    Guider	Kribi	1324	
    Guider	Kumba	1181	
    Guider	Kumbo	961	
    Guider	Limbe	1271	
    Guider	Mamfe	1166	
    Guider	Maroua	97	
    Guider	Mbalmayo	1106	
    Guider	Mbengwi	1086	
    Guider	Mbouda	1014	
    Guider	Meiganga	536	
    Guider	Mfou	1088	
    Guider	Mokolo	105	
    Guider	Monatele	1054	
    Guider	Mora	158	
    Guider	Mundemba	1265	
    Guider	Nanga-Eboko	1050	
    Guider	Ndop	1029	
    Guider	Ngaoundere	380	
    Guider	Ngoumou	1108	
    Guider	Nkambe	973	
    Guider	Nkongsamba	1101	
    Guider	Ntui	978	
    Guider	Poli	241	
    Guider	Sangmelima	1218	
    Guider	Tchollire	285	
    Guider	Tibati	598	
    Guider	Tignere	510	
    Guider	Wum	1082	
    Guider	Yabassi	1192	
    Guider	Yagoua	189	
    Guider	Yaounde	1059	
    Guider	Yokadouma	1046	
    Kaele	Kousseri	329	
    Kaele	Kribi	1400	
    Kaele	Kumba	1257	
    Kaele	Kumbo	1037	
    Kaele	Limbe	1347	
    Kaele	Mamfe	1242	
    Kaele	Maroua	72	
    Kaele	Mbalmayo	1182	
    Kaele	Mbengwi	1162	
    Kaele	Mbouda	1090	
    Kaele	Meiganga	612	
    Kaele	Mfou	1164	
    Kaele	Mokolo	147	
    Kaele	Monatele	1129	
    Kaele	Mora	133	
    Kaele	Mundemba	1340	
    Kaele	Nanga-Eboko	1126	
    Kaele	Ndop	1105	
    Kaele	Ngaoundere	456	
    Kaele	Ngoumou	1184	
    Kaele	Nkambe	1048	
    Kaele	Nkongsamba	1176	
    Kaele	Ntui	1054	
    Kaele	Poli	316	
    Kaele	Sangmelima	1294	
    Kaele	Tchollire	361	
    Kaele	Tibati	674	
    Kaele	Tignere	585	
    Kaele	Wum	1158	
    Kaele	Yabassi	1268	
    Kaele	Yagoua	109	
    Kaele	Yaounde	1135	
    Kaele	Yokadouma	1122	
    Kousseri	Kribi	1686	
    Kousseri	Kumba	1543	
    Kousseri	Kumbo	1323	
    Kousseri	Limbe	1633	
    Kousseri	Mamfe	1528	
    Kousseri	Maroua	265	
    Kousseri	Mbalmayo	1468	
    Kousseri	Mbengwi	1448	
    Kousseri	Mbouda	1376	
    Kousseri	Meiganga	898	
    Kousseri	Mfou	1450	
    Kousseri	Mokolo	276	
    Kousseri	Monatele	1416	
    Kousseri	Mora	209	
    Kousseri	Mundemba	1627	
    Kousseri	Nanga-Eboko	1412	
    Kousseri	Ndop	1391	
    Kousseri	Ngaoundere	742	
    Kousseri	Ngoumou	1470	
    Kousseri	Nkambe	1335	
    Kousseri	Nkongsamba	1463	
    Kousseri	Ntui	1340	
    Kousseri	Poli	603	
    Kousseri	Sangmelima	1580	
    Kousseri	Tchollire	647	
    Kousseri	Tibati	960	
    Kousseri	Tignere	872	
    Kousseri	Wum	1444	
    Kousseri	Yabassi	1554	
    Kousseri	Yagoua	241	
    Kousseri	Yaounde	1421	
    Kousseri	Yokadouma	1408	
    Kribi	Kumba	311	
    Kribi	Kumbo	531	
    Kribi	Limbe	249	
    Kribi	Mamfe	459	
    Kribi	Maroua	1421	
    Kribi	Mbalmayo	244	
    Kribi	Mbengwi	494	
    Kribi	Mbouda	420	
    Kribi	Meiganga	949	
    Kribi	Mfou	288	
    Kribi	Mokolo	1429	
    Kribi	Monatele	295	
    Kribi	Mora	1482	
    Kribi	Mundemba	397	
    Kribi	Nanga-Eboko	444	
    Kribi	Ndop	480	
    Kribi	Ngaoundere	946	
    Kribi	Ngoumou	232	
    Kribi	Nkambe	600	
    Kribi	Nkongsamba	311	
    Kribi	Ntui	361	
    Kribi	Poli	1160	
    Kribi	Sangmelima	290	
    Kribi	Tchollire	1163	
    Kribi	Tibati	726	
    Kribi	Tignere	877	
    Kribi	Wum	547	
    Kribi	Yabassi	214	
    Kribi	Yagoua	1508	
    Kribi	Yaounde	280	
    Kribi	Yokadouma	862	
    Kumba	Kumbo	317	
    Kumba	Limbe	91	
    Kumba	Mamfe	159	
    Kumba	Maroua	1278	
    Kumba	Mbalmayo	412	
    Kumba	Mbengwi	264	
    Kumba	Mbouda	190	
    Kumba	Meiganga	834	
    Kumba	Mfou	396	
    Kumba	Mokolo	1286	
    Kumba	Monatele	370	
    Kumba	Mora	1339	
    Kumba	Mundemba	113	
    Kumba	Nanga-Eboko	507	
    Kumba	Ndop	249	
    Kumba	Ngaoundere	803	
    Kumba	Ngoumou	390	
    Kumba	Nkambe	386	
    Kumba	Nkongsamba	80	
    Kumba	Ntui	381	
    Kumba	Poli	1017	
    Kumba	Sangmelima	526	
    Kumba	Tchollire	1019	
    Kumba	Tibati	583	
    Kumba	Tignere	734	
    Kumba	Wum	316	
    Kumba	Yabassi	97	
    Kumba	Yagoua	1365	
    Kumba	Yaounde	374	
    Kumba	Yokadouma	955	
    Kumbo	Limbe	408	
    Kumbo	Mamfe	242	
    Kumbo	Maroua	1058	
    Kumbo	Mbalmayo	430	
    Kumbo	Mbengwi	125	
    Kumbo	Mbouda	128	
    Kumbo	Meiganga	614	
    Kumbo	Mfou	411	
    Kumbo	Mokolo	1066	
    Kumbo	Monatele	325	
    Kumbo	Mora	1119	
    Kumbo	Mundemba	388	
    Kumbo	Nanga-Eboko	461	
    Kumbo	Ndop	68	
    Kumbo	Ngaoundere	583	
    Kumbo	Ngoumou	431	
    Kumbo	Nkambe	68	
    Kumbo	Nkongsamba	237	
    Kumbo	Ntui	336	
    Kumbo	Poli	797	
    Kumbo	Sangmelima	541	
    Kumbo	Tchollire	800	
    Kumbo	Tibati	363	
    Kumbo	Tignere	514	
    Kumbo	Wum	127	
    Kumbo	Yabassi	334	
    Kumbo	Yagoua	1145	
    Kumbo	Yaounde	383	
    Kumbo	Yokadouma	909	
    Limbe	Mamfe	250	
    Limbe	Maroua	1368	
    Limbe	Mbalmayo	350	
    Limbe	Mbengwi	354	
    Limbe	Mbouda	280	
    Limbe	Meiganga	924	
    Limbe	Mfou	334	
    Limbe	Mokolo	1376	
    Limbe	Monatele	323	
    Limbe	Mora	1429	
    Limbe	Mundemba	171	
    Limbe	Nanga-Eboko	473	
    Limbe	Ndop	340	
    Limbe	Ngaoundere	893	
    Limbe	Ngoumou	328	
    Limbe	Nkambe	476	
    Limbe	Nkongsamba	170	
    Limbe	Ntui	390	
    Limbe	Poli	1107	
    Limbe	Sangmelima	463	
    Limbe	Tchollire	1110	
    Limbe	Tibati	673	
    Limbe	Tignere	824	
    Limbe	Wum	407	
    Limbe	Yabassi	167	
    Limbe	Yagoua	1455	
    Limbe	Yaounde	311	
    Limbe	Yokadouma	900	
    Mamfe	Maroua	1263	
    Mamfe	Mbalmayo	518	
    Mamfe	Mbengwi	128	
    Mamfe	Mbouda	166	
    Mamfe	Meiganga	819	
    Mamfe	Mfou	499	
    Mamfe	Mokolo	1271	
    Mamfe	Monatele	412	
    Mamfe	Mora	1324	
    Mamfe	Mundemba	167	
    Mamfe	Nanga-Eboko	549	
    Mamfe	Ndop	177	
    Mamfe	Ngaoundere	788	
    Mamfe	Ngoumou	519	
    Mamfe	Nkambe	311	
    Mamfe	Nkongsamba	201	
    Mamfe	Ntui	424	
    Mamfe	Poli	1002	
    Mamfe	Sangmelima	629	
    Mamfe	Tchollire	1005	
    Mamfe	Tibati	568	
    Mamfe	Tignere	719	
    Mamfe	Wum	214	
    Mamfe	Yabassi	245	
    Mamfe	Yagoua	1350	
    Mamfe	Yaounde	470	
    Mamfe	Yokadouma	997	
    Maroua	Mbalmayo	1203	
    Maroua	Mbengwi	1183	
    Maroua	Mbouda	1111	
    Maroua	Meiganga	633	
    Maroua	Mfou	1185	
    Maroua	Mokolo	75	
    Maroua	Monatele	1151	
    Maroua	Mora	61	
    Maroua	Mundemba	1362	
    Maroua	Nanga-Eboko	1148	
    Maroua	Ndop	1126	
    Maroua	Ngaoundere	477	
    Maroua	Ngoumou	1205	
    Maroua	Nkambe	1070	
    Maroua	Nkongsamba	1198	
    Maroua	Ntui	1075	
    Maroua	Poli	338	
    Maroua	Sangmelima	1315	
    Maroua	Tchollire	382	
    Maroua	Tibati	696	
    Maroua	Tignere	607	
    Maroua	Wum	1179	
    Maroua	Yabassi	1290	
    Maroua	Yagoua	120	
    Maroua	Yaounde	1156	
    Maroua	Yokadouma	1144	
    Mbalmayo	Mbengwi	443	
    Mbalmayo	Mbouda	369	
    Mbalmayo	Meiganga	705	
    Mbalmayo	Mfou	44	
    Mbalmayo	Mokolo	1211	
    Mbalmayo	Monatele	123	
    Mbalmayo	Mora	1264	
    Mbalmayo	Mundemba	498	
    Mbalmayo	Nanga-Eboko	210	
    Mbalmayo	Ndop	414	
    Mbalmayo	Ngaoundere	729	
    Mbalmayo	Ngoumou	37	
    Mbalmayo	Nkambe	498	
    Mbalmayo	Nkongsamba	395	
    Mbalmayo	Ntui	128	
    Mbalmayo	Poli	942	
    Mbalmayo	Sangmelima	122	
    Mbalmayo	Tchollire	945	
    Mbalmayo	Tibati	508	
    Mbalmayo	Tignere	660	
    Mbalmayo	Wum	496	
    Mbalmayo	Yabassi	315	
    Mbalmayo	Yagoua	1291	
    Mbalmayo	Yaounde	47	
    Mbalmayo	Yokadouma	619	
    Mbengwi	Mbouda	74	
    Mbengwi	Meiganga	739	
    Mbengwi	Mfou	425	
    Mbengwi	Mokolo	1191	
    Mbengwi	Monatele	338	
    Mbengwi	Mora	1244	
    Mbengwi	Mundemba	273	
    Mbengwi	Nanga-Eboko	475	
    Mbengwi	Ndop	60	
    Mbengwi	Ngaoundere	708	
    Mbengwi	Ngoumou	445	
    Mbengwi	Nkambe	193	
    Mbengwi	Nkongsamba	183	
    Mbengwi	Ntui	349	
    Mbengwi	Poli	922	
    Mbengwi	Sangmelima	555	
    Mbengwi	Tchollire	925	
    Mbengwi	Tibati	488	
    Mbengwi	Tignere	639	
    Mbengwi	Wum	97	
    Mbengwi	Yabassi	280	
    Mbengwi	Yagoua	1270	
    Mbengwi	Yaounde	396	
    Mbengwi	Yokadouma	923	
    Mbouda	Meiganga	667	
    Mbouda	Mfou	351	
    Mbouda	Mokolo	1119	
    Mbouda	Monatele	264	
    Mbouda	Mora	1172	
    Mbouda	Mundemba	265	
    Mbouda	Nanga-Eboko	401	
    Mbouda	Ndop	60	
    Mbouda	Ngaoundere	636	
    Mbouda	Ngoumou	371	
    Mbouda	Nkambe	196	
    Mbouda	Nkongsamba	110	
    Mbouda	Ntui	275	
    Mbouda	Poli	850	
    Mbouda	Sangmelima	481	
    Mbouda	Tchollire	853	
    Mbouda	Tibati	416	
    Mbouda	Tignere	567	
    Mbouda	Wum	127	
    Mbouda	Yabassi	206	
    Mbouda	Yagoua	1198	
    Mbouda	Yaounde	322	
    Mbouda	Yokadouma	849	
    Meiganga	Mfou	679	
    Meiganga	Mokolo	641	
    Meiganga	Monatele	687	
    Meiganga	Mora	694	
    Meiganga	Mundemba	918	
    Meiganga	Nanga-Eboko	514	
    Meiganga	Ndop	682	
    Meiganga	Ngaoundere	156	
    Meiganga	Ngoumou	720	
    Meiganga	Nkambe	626	
    Meiganga	Nkongsamba	754	
    Meiganga	Ntui	632	
    Meiganga	Poli	372	
    Meiganga	Sangmelima	710	
    Meiganga	Tchollire	375	
    Meiganga	Tibati	252	
    Meiganga	Tignere	288	
    Meiganga	Wum	735	
    Meiganga	Yabassi	846	
    Meiganga	Yagoua	721	
    Meiganga	Yaounde	678	
    Meiganga	Yokadouma	510	
    Mfou	Mokolo	1193	
    Mfou	Monatele	105	
    Mfou	Mora	1246	
    Mfou	Mundemba	482	
    Mfou	Nanga-Eboko	192	
    Mfou	Ndop	396	
    Mfou	Ngaoundere	711	
    Mfou	Ngoumou	66	
    Mfou	Nkambe	480	
    Mfou	Nkongsamba	377	
    Mfou	Ntui	110	
    Mfou	Poli	924	
    Mfou	Sangmelima	130	
    Mfou	Tchollire	927	
    Mfou	Tibati	490	
    Mfou	Tignere	641	
    Mfou	Wum	478	
    Mfou	Yabassi	299	
    Mfou	Yagoua	1273	
    Mfou	Yaounde	29	
    Mfou	Yokadouma	592	
    Mokolo	Monatele	1159	
    Mokolo	Mora	67	
    Mokolo	Mundemba	1369	
    Mokolo	Nanga-Eboko	1155	
    Mokolo	Ndop	1134	
    Mokolo	Ngaoundere	485	
    Mokolo	Ngoumou	1213	
    Mokolo	Nkambe	1077	
    Mokolo	Nkongsamba	1205	
    Mokolo	Ntui	1083	
    Mokolo	Poli	345	
    Mokolo	Sangmelima	1323	
    Mokolo	Tchollire	390	
    Mokolo	Tibati	703	
    Mokolo	Tignere	614	
    Mokolo	Wum	1187	
    Mokolo	Yabassi	1297	
    Mokolo	Yagoua	196	
    Mokolo	Yaounde	1164	
    Mokolo	Yokadouma	1151	
    Monatele	Mora	1213	
    Monatele	Mundemba	471	
    Monatele	Nanga-Eboko	174	
    Monatele	Ndop	310	
    Monatele	Ngaoundere	676	
    Monatele	Ngoumou	124	
    Monatele	Nkambe	394	
    Monatele	Nkongsamba	291	
    Monatele	Ntui	77	
    Monatele	Poli	891	
    Monatele	Sangmelima	234	
    Monatele	Tchollire	894	
    Monatele	Tibati	457	
    Monatele	Tignere	608	
    Monatele	Wum	392	
    Monatele	Yabassi	282	
    Monatele	Yagoua	1239	
    Monatele	Yaounde	76	
    Monatele	Yokadouma	622	
    Mora	Mundemba	1423	
    Mora	Nanga-Eboko	1208	
    Mora	Ndop	1187	
    Mora	Ngaoundere	538	
    Mora	Ngoumou	1266	
    Mora	Nkambe	1131	
    Mora	Nkongsamba	1259	
    Mora	Ntui	1136	
    Mora	Poli	399	
    Mora	Sangmelima	1376	
    Mora	Tchollire	443	
    Mora	Tibati	756	
    Mora	Tignere	668	
    Mora	Wum	1240	
    Mora	Yabassi	1350	
    Mora	Yagoua	181	
    Mora	Yaounde	1217	
    Mora	Yokadouma	1205	
    Mundemba	Nanga-Eboko	619	
    Mundemba	Ndop	323	
    Mundemba	Ngaoundere	887	
    Mundemba	Ngoumou	476	
    Mundemba	Nkambe	456	
    Mundemba	Nkongsamba	193	
    Mundemba	Ntui	494	
    Mundemba	Poli	1100	
    Mundemba	Sangmelima	612	
    Mundemba	Tchollire	1103	
    Mundemba	Tibati	667	
    Mundemba	Tignere	818	
    Mundemba	Wum	359	
    Mundemba	Yabassi	209	
    Mundemba	Yagoua	1449	
    Mundemba	Yaounde	460	
    Mundemba	Yokadouma	1048	
    Nanga-Eboko	Ndop	446	
    Nanga-Eboko	Ngaoundere	670	
    Nanga-Eboko	Ngoumou	212	
    Nanga-Eboko	Nkambe	530	
    Nanga-Eboko	Nkongsamba	426	
    Nanga-Eboko	Ntui	125	
    Nanga-Eboko	Poli	886	
    Nanga-Eboko	Sangmelima	267	
    Nanga-Eboko	Tchollire	889	
    Nanga-Eboko	Tibati	505	
    Nanga-Eboko	Tignere	657	
    Nanga-Eboko	Wum	527	
    Nanga-Eboko	Yabassi	425	
    Nanga-Eboko	Yagoua	1235	
    Nanga-Eboko	Yaounde	163	
    Nanga-Eboko	Yokadouma	448	
    Ndop	Ngaoundere	651	
    Ndop	Ngoumou	416	
    Ndop	Nkambe	137	
    Ndop	Nkongsamba	169	
    Ndop	Ntui	320	
    Ndop	Poli	865	
    Ndop	Sangmelima	526	
    Ndop	Tchollire	868	
    Ndop	Tibati	431	
    Ndop	Tignere	582	
    Ndop	Wum	96	
    Ndop	Yabassi	266	
    Ndop	Yagoua	1213	
    Ndop	Yaounde	367	
    Ndop	Yokadouma	894	
    Ngaoundere	Ngoumou	730	
    Ngaoundere	Nkambe	595	
    Ngaoundere	Nkongsamba	723	
    Ngaoundere	Ntui	601	
    Ngaoundere	Poli	216	
    Ngaoundere	Sangmelima	841	
    Ngaoundere	Tchollire	219	
    Ngaoundere	Tibati	221	
    Ngaoundere	Tignere	132	
    Ngaoundere	Wum	704	
    Ngaoundere	Yabassi	815	
    Ngaoundere	Yagoua	564	
    Ngaoundere	Yaounde	682	
    Ngaoundere	Yokadouma	667	
    Ngoumou	Nkambe	500	
    Ngoumou	Nkongsamba	390	
    Ngoumou	Ntui	130	
    Ngoumou	Poli	944	
    Ngoumou	Sangmelima	159	
    Ngoumou	Tchollire	947	
    Ngoumou	Tibati	509	
    Ngoumou	Tignere	661	
    Ngoumou	Wum	497	
    Ngoumou	Yabassi	293	
    Ngoumou	Yagoua	1292	
    Ngoumou	Yaounde	49	
    Ngoumou	Yokadouma	633	
    Nkambe	Nkongsamba	306	
    Nkambe	Ntui	404	
    Nkambe	Poli	808	
    Nkambe	Sangmelima	610	
    Nkambe	Tchollire	811	
    Nkambe	Tibati	374	
    Nkambe	Tignere	526	
    Nkambe	Wum	109	
    Nkambe	Yabassi	402	
    Nkambe	Yagoua	1157	
    Nkambe	Yaounde	451	
    Nkambe	Yokadouma	978	
    Nkongsamba	Ntui	301	
    Nkongsamba	Poli	936	
    Nkongsamba	Sangmelima	507	
    Nkongsamba	Tchollire	939	
    Nkongsamba	Tibati	503	
    Nkongsamba	Tignere	654	
    Nkongsamba	Wum	236	
    Nkongsamba	Yabassi	96	
    Nkongsamba	Yagoua	1285	
    Nkongsamba	Yaounde	348	
    Nkongsamba	Yokadouma	875	
    Ntui	Poli	814	
    Ntui	Sangmelima	240	
    Ntui	Tchollire	817	
    Ntui	Tibati	380	
    Ntui	Tignere	532	
    Ntui	Wum	402	
    Ntui	Yabassi	300	
    Ntui	Yagoua	1163	
    Ntui	Yaounde	81	
    Ntui	Yokadouma	574	
    Poli	Sangmelima	1054	
    Poli	Tchollire	134	
    Poli	Tibati	434	
    Poli	Tignere	345	
    Poli	Wum	918	
    Poli	Yabassi	1028	
    Poli	Yagoua	425	
    Poli	Yaounde	895	
    Poli	Yokadouma	882	
    Sangmelima	Tchollire	1057	
    Sangmelima	Tibati	620	
    Sangmelima	Tignere	771	
    Sangmelima	Wum	608	
    Sangmelima	Yabassi	429	
    Sangmelima	Yagoua	1403	
    Sangmelima	Yaounde	159	
    Sangmelima	Yokadouma	623	
    Tchollire	Tibati	437	
    Tchollire	Tignere	348	
    Tchollire	Wum	921	
    Tchollire	Yabassi	1031	
    Tchollire	Yagoua	470	
    Tchollire	Yaounde	898	
    Tchollire	Yokadouma	885	
    Tibati	Tignere	152	
    Tibati	Wum	484	
    Tibati	Yabassi	594	
    Tibati	Yagoua	783	
    Tibati	Yaounde	461	
    Tibati	Yokadouma	742	
    Tignere	Wum	635	
    Tignere	Yabassi	746	
    Tignere	Yagoua	694	
    Tignere	Yaounde	613	
    Tignere	Yokadouma	799	
    Wum	Yabassi	333	
    Wum	Yagoua	1266	
    Wum	Yaounde	449	
    Wum	Yokadouma	976	
    Yabassi	Yagoua	1377	
    Yabassi	Yaounde	277	
    Yabassi	Yokadouma	865	
    Yagoua	Yaounde	1244	
    Yagoua	Yokadouma	1231	
    Yaounde	Yokadouma	595
"""

    /**
     * List
     */
    const val journeyDistanceList = """
    Abong-Mbang	+	Akonolinga	+	131
    Abong-Mbang	+	Ambam	+	433
    Abong-Mbang	+	Bafang	+	518
    Abong-Mbang	+	Bafia	+	348
    Abong-Mbang	+	Bafoussam	+	518
    Abong-Mbang	+	Baham	+	511
    Abong-Mbang	+	Bamenda	+	597
    Abong-Mbang	+	Bandjoun	+	504
    Abong-Mbang	+	Bangangte	+	471
    Abong-Mbang	+	Bangem	+	582
    Abong-Mbang	+	Banyo	+	734
    Abong-Mbang	+	Batouri	+	179
    Abong-Mbang	+	Bertoua	+	111
    Abong-Mbang	+	Buea	+	532
    Abong-Mbang	+	Douala	+	467
    Abong-Mbang	+	Dschang	+	565
    Abong-Mbang	+	Ebolowa	+	344
    Abong-Mbang	+	Edea	+	397
    Abong-Mbang	+	Eseka	+	346
    Abong-Mbang	+	Fontem	+	606
    Abong-Mbang	+	Foumban	+	524
    Abong-Mbang	+	Fundong	+	662
    Abong-Mbang	+	Garoua	+	884
    Abong-Mbang	+	Guider	+	987
    Abong-Mbang	+	Kaele	+	1063
    Abong-Mbang	+	Kousseri	+	1349
    Abong-Mbang	+	Kribi	+	498
    Abong-Mbang	+	Kumba	+	598
    Abong-Mbang	+	Kumbo	+	606
    Abong-Mbang	+	Limbe	+	536
    Abong-Mbang	+	Mamfe	+	694
    Abong-Mbang	+	Maroua	+	1085
    Abong-Mbang	+	Mbalmayo	+	254
    Abong-Mbang	+	Mbengwi	+	619
    Abong-Mbang	+	Mbouda	+	546
    Abong-Mbang	+	Meiganga	+	451
    Abong-Mbang	+	Mfou	+	227
    Abong-Mbang	+	Mokolo	+	1092
    Abong-Mbang	+	Monatele	+	306
    Abong-Mbang	+	Mora	+	1145
    Abong-Mbang	+	Mundemba	+	684
    Abong-Mbang	+	Nanga-Eboko	+	201
    Abong-Mbang	+	Ndop	+	591
    Abong-Mbang	+	Ngaoundere	+	607
    Abong-Mbang	+	Ngoumou	+	268
    Abong-Mbang	+	Nkambe	+	674
    Abong-Mbang	+	Nkongsamba	+	571
    Abong-Mbang	+	Ntui	+	284
    Abong-Mbang	+	Poli	+	823
    Abong-Mbang	+	Sangmelima	+	259
    Abong-Mbang	+	Tchollire	+	826
    Abong-Mbang	+	Tibati	+	664
    Abong-Mbang	+	Tignere	+	739
    Abong-Mbang	+	Wum	+	672
    Abong-Mbang	+	Yabassi	+	501
    Abong-Mbang	+	Yagoua	+	1172
    Abong-Mbang	+	Yaounde	+	231
    Abong-Mbang	+	Yokadouma	+	364
    Akonolinga	+	Ambam	+	302
    Akonolinga	+	Bafang	+	403
    Akonolinga	+	Bafia	+	234
    Akonolinga	+	Bafoussam	+	403
    Akonolinga	+	Baham	+	397
    Akonolinga	+	Bamenda	+	483
    Akonolinga	+	Bandjoun	+	390
    Akonolinga	+	Bangangte	+	356
    Akonolinga	+	Bangem	+	467
    Akonolinga	+	Banyo	+	620
    Akonolinga	+	Batouri	+	310
    Akonolinga	+	Bertoua	+	242
    Akonolinga	+	Buea	+	417
    Akonolinga	+	Douala	+	353
    Akonolinga	+	Dschang	+	451
    Akonolinga	+	Ebolowa	+	212
    Akonolinga	+	Edea	+	283
    Akonolinga	+	Eseka	+	231
    Akonolinga	+	Fontem	+	492
    Akonolinga	+	Foumban	+	410
    Akonolinga	+	Fundong	+	547
    Akonolinga	+	Garoua	+	1015
    Akonolinga	+	Guider	+	1119
    Akonolinga	+	Kaele	+	1194
    Akonolinga	+	Kousseri	+	1481
    Akonolinga	+	Kribi	+	484
    Akonolinga	+	Kumba	+	421
    Akonolinga	+	Kumbo	+	491
    Akonolinga	+	Limbe	+	421
    Akonolinga	+	Mamfe	+	579
    Akonolinga	+	Maroua	+	1216
    Akonolinga	+	Mbalmayo	+	123
    Akonolinga	+	Mbengwi	+	505
    Akonolinga	+	Mbouda	+	431
    Akonolinga	+	Meiganga	+	582
    Akonolinga	+	Mfou	+	113
    Akonolinga	+	Mokolo	+	1223
    Akonolinga	+	Monatele	+	191
    Akonolinga	+	Mora	+	1277
    Akonolinga	+	Mundemba	+	570
    Akonolinga	+	Nanga-Eboko	+	138
    Akonolinga	+	Ndop	+	476
    Akonolinga	+	Ngaoundere	+	739
    Akonolinga	+	Ngoumou	+	154
    Akonolinga	+	Nkambe	+	560
    Akonolinga	+	Nkongsamba	+	457
    Akonolinga	+	Ntui	+	170
    Akonolinga	+	Poli	+	954
    Akonolinga	+	Sangmelima	+	129
    Akonolinga	+	Tchollire	+	957
    Akonolinga	+	Tibati	+	550
    Akonolinga	+	Tignere	+	702
    Akonolinga	+	Wum	+	558
    Akonolinga	+	Yabassi	+	387
    Akonolinga	+	Yagoua	+	1303
    Akonolinga	+	Yaounde	+	117
    Akonolinga	+	Yokadouma	+	496
    Ambam	+	Bafang	+	518
    Ambam	+	Bafia	+	366
    Ambam	+	Bafoussam	+	518
    Ambam	+	Baham	+	511
    Ambam	+	Bamenda	+	597
    Ambam	+	Bandjoun	+	504
    Ambam	+	Bangangte	+	471
    Ambam	+	Bangem	+	542
    Ambam	+	Banyo	+	752
    Ambam	+	Batouri	+	612
    Ambam	+	Bertoua	+	544
    Ambam	+	Buea	+	455
    Ambam	+	Douala	+	390
    Ambam	+	Dschang	+	565
    Ambam	+	Ebolowa	+	89
    Ambam	+	Edea	+	320
    Ambam	+	Eseka	+	222
    Ambam	+	Fontem	+	606
    Ambam	+	Foumban	+	542
    Ambam	+	Fundong	+	662
    Ambam	+	Garoua	+	1197
    Ambam	+	Guider	+	1301
    Ambam	+	Kaele	+	1376
    Ambam	+	Kousseri	+	1663
    Ambam	+	Kribi	+	255
    Ambam	+	Kumba	+	521
    Ambam	+	Kumbo	+	624
    Ambam	+	Limbe	+	459
    Ambam	+	Mamfe	+	669
    Ambam	+	Maroua	+	1398
    Ambam	+	Mbalmayo	+	194
    Ambam	+	Mbengwi	+	619
    Ambam	+	Mbouda	+	546
    Ambam	+	Meiganga	+	884
    Ambam	+	Mfou	+	239
    Ambam	+	Mokolo	+	1405
    Ambam	+	Monatele	+	317
    Ambam	+	Mora	+	1459
    Ambam	+	Mundemba	+	607
    Ambam	+	Nanga-Eboko	+	405
    Ambam	+	Ndop	+	605
    Ambam	+	Ngaoundere	+	923
    Ambam	+	Ngoumou	+	231
    Ambam	+	Nkambe	+	692
    Ambam	+	Nkongsamba	+	520
    Ambam	+	Ntui	+	322
    Ambam	+	Poli	+	1136
    Ambam	+	Sangmelima	+	208
    Ambam	+	Tchollire	+	1139
    Ambam	+	Tibati	+	702
    Ambam	+	Tignere	+	854
    Ambam	+	Wum	+	672
    Ambam	+	Yabassi	+	424
    Ambam	+	Yagoua	+	1485
    Ambam	+	Yaounde	+	241
    Ambam	+	Yokadouma	+	797
    Bafang	+	Bafia	+	169
    Bafang	+	Bafoussam	+	60
    Bafang	+	Baham	+	39
    Bafang	+	Bamenda	+	124
    Bafang	+	Bandjoun	+	46
    Bafang	+	Bangangte	+	47
    Bafang	+	Bangem	+	65
    Bafang	+	Banyo	+	339
    Bafang	+	Batouri	+	636
    Bafang	+	Bertoua	+	547
    Bafang	+	Buea	+	206
    Bafang	+	Douala	+	195
    Bafang	+	Dschang	+	69
    Bafang	+	Ebolowa	+	428
    Bafang	+	Edea	+	250
    Bafang	+	Eseka	+	296
    Bafang	+	Fontem	+	111
    Bafang	+	Foumban	+	128
    Bafang	+	Fundong	+	188
    Bafang	+	Garoua	+	943
    Bafang	+	Guider	+	1047
    Bafang	+	Kaele	+	1123
    Bafang	+	Kousseri	+	1409
    Bafang	+	Kribi	+	361
    Bafang	+	Kumba	+	135
    Bafang	+	Kumbo	+	200
    Bafang	+	Limbe	+	225
    Bafang	+	Mamfe	+	198
    Bafang	+	Maroua	+	1144
    Bafang	+	Mbalmayo	+	341
    Bafang	+	Mbengwi	+	146
    Bafang	+	Mbouda	+	72
    Bafang	+	Meiganga	+	700
    Bafang	+	Mfou	+	323
    Bafang	+	Mokolo	+	1152
    Bafang	+	Monatele	+	236
    Bafang	+	Mora	+	1205
    Bafang	+	Mundemba	+	247
    Bafang	+	Nanga-Eboko	+	373
    Bafang	+	Ndop	+	132
    Bafang	+	Ngaoundere	+	669
    Bafang	+	Ngoumou	+	343
    Bafang	+	Nkambe	+	268
    Bafang	+	Nkongsamba	+	55
    Bafang	+	Ntui	+	248
    Bafang	+	Poli	+	883
    Bafang	+	Sangmelima	+	453
    Bafang	+	Tchollire	+	883
    Bafang	+	Tibati	+	449
    Bafang	+	Tignere	+	600
    Bafang	+	Wum	+	199
    Bafang	+	Yabassi	+	146
    Bafang	+	Yagoua	+	1231
    Bafang	+	Yaounde	+	294
    Bafang	+	Yokadouma	+	821
    Bafia	+	Bafoussam	+	170
    Bafia	+	Baham	+	163
    Bafia	+	Bamenda	+	249
    Bafia	+	Bandjoun	+	156
    Bafia	+	Bangangte	+	122
    Bafia	+	Bangem	+	233
    Bafia	+	Banyo	+	386
    Bafia	+	Batouri	+	474
    Bafia	+	Bertoua	+	385
    Bafia	+	Buea	+	340
    Bafia	+	Douala	+	276
    Bafia	+	Dschang	+	217
    Bafia	+	Ebolowa	+	277
    Bafia	+	Edea	+	212
    Bafia	+	Eseka	+	160
    Bafia	+	Fontem	+	258
    Bafia	+	Foumban	+	176
    Bafia	+	Fundong	+	314
    Bafia	+	Garoua	+	898
    Bafia	+	Guider	+	1002
    Bafia	+	Kaele	+	1078
    Bafia	+	Kousseri	+	1364
    Bafia	+	Kribi	+	322
    Bafia	+	Kumba	+	303
    Bafia	+	Kumbo	+	258
    Bafia	+	Limbe	+	344
    Bafia	+	Mamfe	+	346
    Bafia	+	Maroua	+	1099
    Bafia	+	Mbalmayo	+	172
    Bafia	+	Mbengwi	+	271
    Bafia	+	Mbouda	+	197
    Bafia	+	Meiganga	+	655
    Bafia	+	Mfou	+	154
    Bafia	+	Mokolo	+	1107
    Bafia	+	Monatele	+	67
    Bafia	+	Mora	+	1160
    Bafia	+	Mundemba	+	416
    Bafia	+	Nanga-Eboko	+	211
    Bafia	+	Ndop	+	242
    Bafia	+	Ngaoundere	+	624
    Bafia	+	Ngoumou	+	173
    Bafia	+	Nkambe	+	326
    Bafia	+	Nkongsamba	+	223
    Bafia	+	Ntui	+	85
    Bafia	+	Poli	+	838
    Bafia	+	Sangmelima	+	284
    Bafia	+	Tchollire	+	840
    Bafia	+	Tibati	+	403
    Bafia	+	Tignere	+	555
    Bafia	+	Wum	+	324
    Bafia	+	Yabassi	+	222
    Bafia	+	Yagoua	+	1186
    Bafia	+	Yaounde	+	125
    Bafia	+	Yokadouma	+	659
    Bafoussam	+	Baham	+	21
    Bafoussam	+	Bamenda	+	79
    Bafoussam	+	Bandjoun	+	14
    Bafoussam	+	Bangangte	+	47
    Bafoussam	+	Bangem	+	124
    Bafoussam	+	Banyo	+	280
    Bafoussam	+	Batouri	+	636
    Bafoussam	+	Bertoua	+	547
    Bafoussam	+	Buea	+	265
    Bafoussam	+	Douala	+	253
    Bafoussam	+	Dschang	+	50
    Bafoussam	+	Ebolowa	+	429
    Bafoussam	+	Edea	+	300
    Bafoussam	+	Eseka	+	296
    Bafoussam	+	Fontem	+	91
    Bafoussam	+	Foumban	+	68
    Bafoussam	+	Fundong	+	144
    Bafoussam	+	Garoua	+	884
    Bafoussam	+	Guider	+	987
    Bafoussam	+	Kaele	+	1063
    Bafoussam	+	Kousseri	+	1349
    Bafoussam	+	Kribi	+	411
    Bafoussam	+	Kumba	+	193
    Bafoussam	+	Kumbo	+	143
    Bafoussam	+	Limbe	+	284
    Bafoussam	+	Mamfe	+	179
    Bafoussam	+	Maroua	+	1085
    Bafoussam	+	Mbalmayo	+	342
    Bafoussam	+	Mbengwi	+	101
    Bafoussam	+	Mbouda	+	28
    Bafoussam	+	Meiganga	+	641
    Bafoussam	+	Mfou	+	324
    Bafoussam	+	Mokolo	+	1092
    Bafoussam	+	Monatele	+	236
    Bafoussam	+	Mora	+	1145
    Bafoussam	+	Mundemba	+	277
    Bafoussam	+	Nanga-Eboko	+	373
    Bafoussam	+	Ndop	+	87
    Bafoussam	+	Ngaoundere	+	610
    Bafoussam	+	Ngoumou	+	343
    Bafoussam	+	Nkambe	+	212
    Bafoussam	+	Nkongsamba	+	113
    Bafoussam	+	Ntui	+	248
    Bafoussam	+	Poli	+	823
    Bafoussam	+	Sangmelima	+	453
    Bafoussam	+	Tchollire	+	826
    Bafoussam	+	Tibati	+	389
    Bafoussam	+	Tignere	+	541
    Bafoussam	+	Wum	+	154
    Bafoussam	+	Yabassi	+	205
    Bafoussam	+	Yagoua	+	1172
    Bafoussam	+	Yaounde	+	295
    Bafoussam	+	Yokadouma	+	822
    Baham	+	Bamenda	+	100
    Baham	+	Bandjoun	+	7
    Baham	+	Bangangte	+	41
    Baham	+	Bangem	+	103
    Baham	+	Banyo	+	301
    Baham	+	Batouri	+	629
    Baham	+	Bertoua	+	540
    Baham	+	Buea	+	244
    Baham	+	Douala	+	233
    Baham	+	Dschang	+	59
    Baham	+	Ebolowa	+	422
    Baham	+	Edea	+	288
    Baham	+	Eseka	+	289
    Baham	+	Fontem	+	100
    Baham	+	Foumban	+	89
    Baham	+	Fundong	+	165
    Baham	+	Garoua	+	904
    Baham	+	Guider	+	1008
    Baham	+	Kaele	+	1084
    Baham	+	Kousseri	+	1370
    Baham	+	Kribi	+	399
    Baham	+	Kumba	+	173
    Baham	+	Kumbo	+	164
    Baham	+	Limbe	+	263
    Baham	+	Mamfe	+	188
    Baham	+	Maroua	+	1105
    Baham	+	Mbalmayo	+	335
    Baham	+	Mbengwi	+	122
    Baham	+	Mbouda	+	48
    Baham	+	Meiganga	+	662
    Baham	+	Mfou	+	317
    Baham	+	Mokolo	+	1113
    Baham	+	Monatele	+	229
    Baham	+	Mora	+	1166
    Baham	+	Mundemba	+	285
    Baham	+	Nanga-Eboko	+	366
    Baham	+	Ndop	+	108
    Baham	+	Ngaoundere	+	631
    Baham	+	Ngoumou	+	336
    Baham	+	Nkambe	+	232
    Baham	+	Nkongsamba	+	92
    Baham	+	Ntui	+	241
    Baham	+	Poli	+	844
    Baham	+	Sangmelima	+	447
    Baham	+	Tchollire	+	847
    Baham	+	Tibati	+	410
    Baham	+	Tignere	+	561
    Baham	+	Wum	+	175
    Baham	+	Yabassi	+	184
    Baham	+	Yagoua	+	1193
    Baham	+	Yaounde	+	288
    Baham	+	Yokadouma	+	815
    Bamenda	+	Bandjoun	+	93
    Bamenda	+	Bangangte	+	127
    Bamenda	+	Bangem	+	172
    Bamenda	+	Banyo	+	356
    Bamenda	+	Batouri	+	716
    Bamenda	+	Bertoua	+	627
    Bamenda	+	Buea	+	313
    Bamenda	+	Douala	+	302
    Bamenda	+	Dschang	+	89
    Bamenda	+	Ebolowa	+	508
    Bamenda	+	Edea	+	361
    Bamenda	+	Eseka	+	375
    Bamenda	+	Fontem	+	130
    Bamenda	+	Foumban	+	145
    Bamenda	+	Fundong	+	71
    Bamenda	+	Garoua	+	960
    Bamenda	+	Guider	+	1064
    Bamenda	+	Kaele	+	1140
    Bamenda	+	Kousseri	+	1426
    Bamenda	+	Kribi	+	472
    Bamenda	+	Kumba	+	242
    Bamenda	+	Kumbo	+	103
    Bamenda	+	Limbe	+	332
    Bamenda	+	Mamfe	+	139
    Bamenda	+	Maroua	+	1161
    Bamenda	+	Mbalmayo	+	421
    Bamenda	+	Mbengwi	+	22
    Bamenda	+	Mbouda	+	52
    Bamenda	+	Meiganga	+	717
    Bamenda	+	Mfou	+	403
    Bamenda	+	Mokolo	+	1169
    Bamenda	+	Monatele	+	316
    Bamenda	+	Mora	+	1222
    Bamenda	+	Mundemba	+	285
    Bamenda	+	Nanga-Eboko	+	453
    Bamenda	+	Ndop	+	38
    Bamenda	+	Ngaoundere	+	686
    Bamenda	+	Ngoumou	+	423
    Bamenda	+	Nkambe	+	171
    Bamenda	+	Nkongsamba	+	161
    Bamenda	+	Ntui	+	327
    Bamenda	+	Poli	+	900
    Bamenda	+	Sangmelima	+	533
    Bamenda	+	Tchollire	+	903
    Bamenda	+	Tibati	+	466
    Bamenda	+	Tignere	+	617
    Bamenda	+	Wum	+	75
    Bamenda	+	Yabassi	+	258
    Bamenda	+	Yagoua	+	1248
    Bamenda	+	Yaounde	+	374
    Bamenda	+	Yokadouma	+	901
    Bandjoun	+	Bangangte	+	34
    Bandjoun	+	Bangem	+	110
    Bandjoun	+	Banyo	+	294
    Bandjoun	+	Batouri	+	622
    Bandjoun	+	Bertoua	+	533
    Bandjoun	+	Buea	+	251
    Bandjoun	+	Douala	+	240
    Bandjoun	+	Dschang	+	61
    Bandjoun	+	Ebolowa	+	415
    Bandjoun	+	Edea	+	287
    Bandjoun	+	Eseka	+	282
    Bandjoun	+	Fontem	+	102
    Bandjoun	+	Foumban	+	82
    Bandjoun	+	Fundong	+	158
    Bandjoun	+	Garoua	+	897
    Bandjoun	+	Guider	+	1001
    Bandjoun	+	Kaele	+	1077
    Bandjoun	+	Kousseri	+	1363
    Bandjoun	+	Kribi	+	397
    Bandjoun	+	Kumba	+	180
    Bandjoun	+	Kumbo	+	157
    Bandjoun	+	Limbe	+	270
    Bandjoun	+	Mamfe	+	190
    Bandjoun	+	Maroua	+	1098
    Bandjoun	+	Mbalmayo	+	328
    Bandjoun	+	Mbengwi	+	115
    Bandjoun	+	Mbouda	+	41
    Bandjoun	+	Meiganga	+	655
    Bandjoun	+	Mfou	+	310
    Bandjoun	+	Mokolo	+	1106
    Bandjoun	+	Monatele	+	222
    Bandjoun	+	Mora	+	1159
    Bandjoun	+	Mundemba	+	289
    Bandjoun	+	Nanga-Eboko	+	359
    Bandjoun	+	Ndop	+	101
    Bandjoun	+	Ngaoundere	+	624
    Bandjoun	+	Ngoumou	+	329
    Bandjoun	+	Nkambe	+	225
    Bandjoun	+	Nkongsamba	+	99
    Bandjoun	+	Ntui	+	234
    Bandjoun	+	Poli	+	837
    Bandjoun	+	Sangmelima	+	440
    Bandjoun	+	Tchollire	+	840
    Bandjoun	+	Tibati	+	403
    Bandjoun	+	Tignere	+	554
    Bandjoun	+	Wum	+	168
    Bandjoun	+	Yabassi	+	191
    Bandjoun	+	Yagoua	+	1186
    Bandjoun	+	Yaounde	+	281
    Bandjoun	+	Yokadouma	+	808
    Bangangte	+	Bangem	+	111
    Bangangte	+	Banyo	+	304
    Bangangte	+	Batouri	+	589
    Bangangte	+	Bertoua	+	500
    Bangangte	+	Buea	+	252
    Bangangte	+	Douala	+	241
    Bangangte	+	Dschang	+	95
    Bangangte	+	Ebolowa	+	381
    Bangangte	+	Edea	+	253
    Bangangte	+	Eseka	+	248
    Bangangte	+	Fontem	+	136
    Bangangte	+	Foumban	+	93
    Bangangte	+	Fundong	+	191
    Bangangte	+	Garoua	+	908
    Bangangte	+	Guider	+	1012
    Bangangte	+	Kaele	+	1088
    Bangangte	+	Kousseri	+	1374
    Bangangte	+	Kribi	+	364
    Bangangte	+	Kumba	+	181
    Bangangte	+	Kumbo	+	168
    Bangangte	+	Limbe	+	271
    Bangangte	+	Mamfe	+	223
    Bangangte	+	Maroua	+	1109
    Bangangte	+	Mbalmayo	+	294
    Bangangte	+	Mbengwi	+	149
    Bangangte	+	Mbouda	+	75
    Bangangte	+	Meiganga	+	666
    Bangangte	+	Mfou	+	276
    Bangangte	+	Mokolo	+	1117
    Bangangte	+	Monatele	+	189
    Bangangte	+	Mora	+	1170
    Bangangte	+	Mundemba	+	293
    Bangangte	+	Nanga-Eboko	+	326
    Bangangte	+	Ndop	+	135
    Bangangte	+	Ngaoundere	+	635
    Bangangte	+	Ngoumou	+	296
    Bangangte	+	Nkambe	+	236
    Bangangte	+	Nkambe	+	236
    Bangangte	+	Nkongsamba	+	101
    Bangangte	+	Ntui	+	200
    Bangangte	+	Poli	+	848
    Bangangte	+	Sangmelima	+	406
    Bangangte	+	Tchollire	+	851
    Bangangte	+	Tibati	+	414
    Bangangte	+	Tignere	+	565
    Bangangte	+	Wum	+	202
    Bangangte	+	Yabassi	+	193
    Bangangte	+	Yagoua	+	1197
    Bangangte	+	Yaounde	+	247
    Bangangte	+	Yokadouma	+	774
    Bangem	+	Banyo	+	403
    Bangem	+	Batouri	+	700
    Bangem	+	Bertoua	+	611
    Bangem	+	Buea	+	163
    Bangem	+	Douala	+	162
    Bangem	+	Dschang	+	83
    Bangem	+	Ebolowa	+	453
    Bangem	+	Edea	+	222
    Bangem	+	Eseka	+	329
    Bangem	+	Fontem	+	124
    Bangem	+	Foumban	+	191
    Bangem	+	Fundong	+	236
    Bangem	+	Garoua	+	1007
    Bangem	+	Guider	+	1111
    Bangem	+	Kaele	+	1187
    Bangem	+	Kousseri	+	1473
    Bangem	+	Kribi	+	332
    Bangem	+	Kumba	+	91
    Bangem	+	Kumbo	+	248
    Bangem	+	Limbe	+	182
    Bangem	+	Mamfe	+	211
    Bangem	+	Maroua	+	1208
    Bangem	+	Mbalmayo	+	405
    Bangem	+	Mbengwi	+	194
    Bangem	+	Mbouda	+	120
    Bangem	+	Meiganga	+	764
    Bangem	+	Mfou	+	387
    Bangem	+	Mokolo	+	1216
    Bangem	+	Monatele	+	300
    Bangem	+	Mora	+	1269
    Bangem	+	Mundemba	+	204
    Bangem	+	Nanga-Eboko	+	437
    Bangem	+	Ndop	+	179
    Bangem	+	Ngaoundere	+	733
    Bangem	+	Ngoumou	+	407
    Bangem	+	Nkambe	+	316
    Bangem	+	Nkongsamba	+	55
    Bangem	+	Ntui	+	311
    Bangem	+	Poli	+	947
    Bangem	+	Sangmelima	+	517
    Bangem	+	Tchollire	+	950
    Bangem	+	Tibati	+	513
    Bangem	+	Tignere	+	664
    Bangem	+	Wum	+	246
    Bangem	+	Yabassi	+	118
    Bangem	+	Yagoua	+	1295
    Bangem	+	Yaounde	+	358
    Bangem	+	Yokadouma	+	885
    Banyo	+	Batouri	+	671
    Banyo	+	Bertoua	+	682
    Banyo	+	Buea	+	544
    Banyo	+	Douala	+	533
    Banyo	+	Dschang	+	330
    Banyo	+	Ebolowa	+	663
    Banyo	+	Edea	+	558
    Banyo	+	Eseka	+	546
    Banyo	+	Fontem	+	371
    Banyo	+	Foumban	+	212
    Banyo	+	Fundong	+	333
    Banyo	+	Garoua	+	604
    Banyo	+	Guider	+	708
    Banyo	+	Kaele	+	783
    Banyo	+	Kousseri	+	1070
    Banyo	+	Kribi	+	668
    Banyo	+	Kumba	+	473
    Banyo	+	Kumbo	+	253
    Banyo	+	Limbe	+	563
    Banyo	+	Mamfe	+	458
    Banyo	+	Maroua	+	805
    Banyo	+	Mbalmayo	+	558
    Banyo	+	Mbengwi	+	378
    Banyo	+	Mbouda	+	306
    Banyo	+	Meiganga	+	361
    Banyo	+	Mfou	+	540
    Banyo	+	Mokolo	+	812
    Banyo	+	Monatele	+	453
    Banyo	+	Mora	+	866
    Banyo	+	Mundemba	+	557
    Banyo	+	Nanga-Eboko	+	590
    Banyo	+	Ndop	+	321
    Banyo	+	Ngaoundere	+	330
    Banyo	+	Ngoumou	+	560
    Banyo	+	Nkambe	+	265
    Banyo	+	Nkongsamba	+	393
    Banyo	+	Ntui	+	464
    Banyo	+	Poli	+	543
    Banyo	+	Sangmelima	+	670
    Banyo	+	Tchollire	+	546
    Banyo	+	Tibati	+	110
    Banyo	+	Tignere	+	261
    Banyo	+	Wum	+	374
    Banyo	+	Yabassi	+	485
    Banyo	+	Yagoua	+	892
    Banyo	+	Yaounde	+	511
    Banyo	+	Yokadouma	+	852
    Batouri	+	Bertoua	+	89
    Batouri	+	Buea	+	711
    Batouri	+	Douala	+	646
    Batouri	+	Dschang	+	683
    Batouri	+	Ebolowa	+	523
    Batouri	+	Edea	+	576
    Batouri	+	Eseka	+	525
    Batouri	+	Fontem	+	725
    Batouri	+	Foumban	+	643
    Batouri	+	Fundong	+	780
    Batouri	+	Garoua	+	762
    Batouri	+	Guider	+	865
    Batouri	+	Kaele	+	941
    Batouri	+	Kousseri	+	1227
    Batouri	+	Kribi	+	677
    Batouri	+	Kumba	+	770
    Batouri	+	Kumbo	+	724
    Batouri	+	Limbe	+	715
    Batouri	+	Mamfe	+	812
    Batouri	+	Maroua	+	963
    Batouri	+	Mbalmayo	+	433
    Batouri	+	Mbengwi	+	738
    Batouri	+	Mbouda	+	664
    Batouri	+	Meiganga	+	329
    Batouri	+	Mfou	+	406
    Batouri	+	Mokolo	+	970
    Batouri	+	Monatele	+	436
    Batouri	+	Mora	+	1024
    Batouri	+	Mundemba	+	863
    Batouri	+	Nanga-Eboko	+	263
    Batouri	+	Ndop	+	709
    Batouri	+	Ngaoundere	+	486
    Batouri	+	Ngoumou	+	447
    Batouri	+	Nkambe	+	793
    Batouri	+	Nkongsamba	+	690
    Batouri	+	Ntui	+	388
    Batouri	+	Poli	+	701
    Batouri	+	Sangmelima	+	438
    Batouri	+	Tchollire	+	704
    Batouri	+	Tibati	+	561
    Batouri	+	Tignere	+	618
    Batouri	+	Wum	+	790
    Batouri	+	Yabassi	+	680
    Batouri	+	Yagoua	+	1050
    Batouri	+	Yaounde	+	410
    Batouri	+	Yokadouma	+	185
    Bertoua	+	Buea	+	642
    Bertoua	+	Douala	+	578
    Bertoua	+	Dschang	+	594
    Bertoua	+	Ebolowa	+	454
    Bertoua	+	Edea	+	508
    Bertoua	+	Eseka	+	457
    Bertoua	+	Fontem	+	636
    Bertoua	+	Foumban	+	554
    Bertoua	+	Fundong	+	691
    Bertoua	+	Garoua	+	773
    Bertoua	+	Guider	+	876
    Bertoua	+	Kaele	+	952
    Bertoua	+	Kousseri	+	1238
    Bertoua	+	Kribi	+	608
    Bertoua	+	Kumba	+	681
    Bertoua	+	Kumbo	+	635
    Bertoua	+	Limbe	+	646
    Bertoua	+	Mamfe	+	723
    Bertoua	+	Maroua	+	974
    Bertoua	+	Mbalmayo	+	365
    Bertoua	+	Mbengwi	+	649
    Bertoua	+	Mbouda	+	575
    Bertoua	+	Meiganga	+	340
    Bertoua	+	Mfou	+	338
    Bertoua	+	Mokolo	+	981
    Bertoua	+	Monatele	+	347
    Bertoua	+	Mora	+	1034
    Bertoua	+	Mundemba	+	793
    Bertoua	+	Nanga-Eboko	+	174
    Bertoua	+	Ndop	+	620
    Bertoua	+	Ngaoundere	+	496
    Bertoua	+	Ngoumou	+	379
    Bertoua	+	Nkambe	+	704
    Bertoua	+	Nkongsamba	+	601
    Bertoua	+	Ntui	+	299
    Bertoua	+	Poli	+	712
    Bertoua	+	Sangmelima	+	369
    Bertoua	+	Tchollire	+	715
    Bertoua	+	Tibati	+	572
    Bertoua	+	Tignere	+	629
    Bertoua	+	Wum	+	701
    Bertoua	+	Yabassi	+	599
    Bertoua	+	Yagoua	+	1061
    Bertoua	+	Yaounde	+	337
    Bertoua	+	Yokadouma	+	274
    Buea	+	Douala	+	64
    Buea	+	Dschang	+	224
    Buea	+	Ebolowa	+	365
    Buea	+	Edea	+	134
    Buea	+	Eseka	+	242
    Buea	+	Fontem	+	249
    Buea	+	Foumban	+	333
    Buea	+	Fundong	+	377
    Buea	+	Garoua	+	1148
    Buea	+	Guider	+	1252
    Buea	+	Kaele	+	1328
    Buea	+	Kousseri	+	1614
    Buea	+	Kribi	+	245
    Buea	+	Kumba	+	71
    Buea	+	Kumbo	+	389
    Buea	+	Limbe	+	22
    Buea	+	Mamfe	+	230
    Buea	+	Maroua	+	1349
    Buea	+	Mbalmayo	+	346
    Buea	+	Mbengwi	+	335
    Buea	+	Mbouda	+	261
    Buea	+	Meiganga	+	905
    Buea	+	Mfou	+	330
    Buea	+	Mokolo	+	1357
    Buea	+	Monatele	+	319
    Buea	+	Mora	+	1410
    Buea	+	Mundemba	+	157
    Buea	+	Nanga-Eboko	+	469
    Buea	+	Ndop	+	321
    Buea	+	Ngaoundere	+	874
    Buea	+	Ngoumou	+	324
    Buea	+	Nkambe	+	457
    Buea	+	Nkongsamba	+	151
    Buea	+	Ntui	+	386
    Buea	+	Poli	+	1088
    Buea	+	Sangmelima	+	459
    Buea	+	Tchollire	+	1091
    Buea	+	Tibati	+	654
    Buea	+	Tignere	+	805
    Buea	+	Wum	+	388
    Buea	+	Yabassi	+	163
    Buea	+	Yagoua	+	1438
    Buea	+	Yaounde	+	307
    Buea	+	Yokadouma	+	896
    Douala	+	Dschang	+	213
    Douala	+	Ebolowa	+	301
    Douala	+	Edea	+	70
    Douala	+	Eseka	+	178
    Douala	+	Fontem	+	254
    Douala	+	Foumban	+	321
    Douala	+	Fundong	+	366
    Douala	+	Garoua	+	1137
    Douala	+	Guider	+	1241
    Douala	+	Kaele	+	1317
    Douala	+	Kousseri	+	1603
    Douala	+	Kribi	+	180
    Douala	+	Kumba	+	131
    Douala	+	Kumbo	+	378
    Douala	+	Limbe	+	68
    Douala	+	Mamfe	+	289
    Douala	+	Maroua	+	1338
    Douala	+	Mbalmayo	+	281
    Douala	+	Mbengwi	+	324
    Douala	+	Mbouda	+	250
    Douala	+	Meiganga	+	894
    Douala	+	Mfou	+	265
    Douala	+	Mokolo	+	1346
    Douala	+	Monatele	+	254
    Douala	+	Mora	+	1399
    Douala	+	Mundemba	+	217
    Douala	+	Nanga-Eboko	+	404
    Douala	+	Ndop	+	309
    Douala	+	Ngaoundere	+	863
    Douala	+	Ngoumou	+	260
    Douala	+	Nkambe	+	446
    Douala	+	Nkongsamba	+	140
    Douala	+	Ntui	+	322
    Douala	+	Poli	+	1077
    Douala	+	Sangmelima	+	395
    Douala	+	Tchollire	+	1080
    Douala	+	Tibati	+	643
    Douala	+	Tignere	+	794
    Douala	+	Wum	+	376
    Douala	+	Yabassi	+	98
    Douala	+	Yagoua	+	1425
    Douala	+	Yaounde	+	243
    Douala	+	Yokadouma	+	832
    Dschang	+	Ebolowa	+	476
    Dschang	+	Edea	+	272
    Dschang	+	Eseka	+	343
    Dschang	+	Fontem	+	41
    Dschang	+	Foumban	+	118
    Dschang	+	Fundong	+	154
    Dschang	+	Garoua	+	933
    Dschang	+	Guider	+	1037
    Dschang	+	Kaele	+	1113
    Dschang	+	Kousseri	+	1399
    Dschang	+	Kribi	+	383
    Dschang	+	Kumba	+	153
    Dschang	+	Kumbo	+	165
    Dschang	+	Limbe	+	243
    Dschang	+	Mamfe	+	129
    Dschang	+	Maroua	+	1134
    Dschang	+	Mbalmayo	+	389
    Dschang	+	Mbengwi	+	111
    Dschang	+	Mbouda	+	37
    Dschang	+	Meiganga	+	691
    Dschang	+	Mfou	+	371
    Dschang	+	Mokolo	+	1142
    Dschang	+	Monatele	+	283
    Dschang	+	Mora	+	1195
    Dschang	+	Mundemba	+	227
    Dschang	+	Nanga-Eboko	+	420
    Dschang	+	Ndop	+	97
    Dschang	+	Ngaoundere	+	660
    Dschang	+	Ngoumou	+	390
    Dschang	+	Nkambe	+	233
    Dschang	+	Nkongsamba	+	73
    Dschang	+	Ntui	+	295
    Dschang	+	Poli	+	873
    Dschang	+	Sangmelima	+	501
    Dschang	+	Tchollire	+	876
    Dschang	+	Tibati	+	439
    Dschang	+	Tignere	+	590
    Dschang	+	Wum	+	164
    Dschang	+	Yabassi	+	169
    Dschang	+	Yagoua	+	1222
    Dschang	+	Yaounde	+	342
    Dschang	+	Yokadouma	+	869
    Ebolowa	+	Edea	+	231
    Ebolowa	+	Eseka	+	133
    Ebolowa	+	Fontem	+	517
    Ebolowa	+	Foumban	+	453
    Ebolowa	+	Fundong	+	573
    Ebolowa	+	Garoua	+	1108
    Ebolowa	+	Guider	+	1211
    Ebolowa	+	Kaele	+	1287
    Ebolowa	+	Kousseri	+	1573
    Ebolowa	+	Kribi	+	172
    Ebolowa	+	Kumba	+	432
    Ebolowa	+	Kumbo	+	535
    Ebolowa	+	Limbe	+	369
    Ebolowa	+	Mamfe	+	580
    Ebolowa	+	Maroua	+	1308
    Ebolowa	+	Mbalmayo	+	105
    Ebolowa	+	Mbengwi	+	530
    Ebolowa	+	Mbouda	+	456
    Ebolowa	+	Meiganga	+	795
    Ebolowa	+	Mfou	+	149
    Ebolowa	+	Mokolo	+	1316
    Ebolowa	+	Monatele	+	228
    Ebolowa	+	Mora	+	1369
    Ebolowa	+	Mundemba	+	518
    Ebolowa	+	Nanga-Eboko	+	315
    Ebolowa	+	Ndop	+	516
    Ebolowa	+	Ngaoundere	+	834
    Ebolowa	+	Ngoumou	+	142
    Ebolowa	+	Nkambe	+	603
    Ebolowa	+	Nkongsamba	+	431
    Ebolowa	+	Ntui	+	233
    Ebolowa	+	Poli	+	1047
    Ebolowa	+	Sangmelima	+	119
    Ebolowa	+	Tchollire	+	1050
    Ebolowa	+	Tibati	+	613
    Ebolowa	+	Tignere	+	765
    Ebolowa	+	Wum	+	583
    Ebolowa	+	Yabassi	+	335
    Ebolowa	+	Yagoua	+	1396
    Ebolowa	+	Yaounde	+	152
    Ebolowa	+	Yokadouma	+	708
    Edea	+	Eseka	+	108
    Edea	+	Fontem	+	314
    Edea	+	Foumban	+	346
    Edea	+	Fundong	+	426
    Edea	+	Garoua	+	1110
    Edea	+	Guider	+	1213
    Edea	+	Kaele	+	1289
    Edea	+	Kousseri	+	1575
    Edea	+	Kribi	+	111
    Edea	+	Kumba	+	201
    Edea	+	Kumbo	+	421
    Edea	+	Limbe	+	138
    Edea	+	Mamfe	+	348
    Edea	+	Maroua	+	1311
    Edea	+	Mbalmayo	+	212
    Edea	+	Mbengwi	+	383
    Edea	+	Mbouda	+	310
    Edea	+	Meiganga	+	849
    Edea	+	Mfou	+	195
    Edea	+	Mokolo	+	1318
    Edea	+	Monatele	+	185
    Edea	+	Mora	+	1371
    Edea	+	Mundemba	+	287
    Edea	+	Nanga-Eboko	+	335
    Edea	+	Ndop	+	369
    Edea	+	Ngaoundere	+	836
    Edea	+	Ngoumou	+	190
    Edea	+	Nkambe	+	489
    Edea	+	Nkongsamba	+	200
    Edea	+	Ntui	+	252
    Edea	+	Poli	+	1049
    Edea	+	Sangmelima	+	325
    Edea	+	Tchollire	+	1052
    Edea	+	Tibati	+	615
    Edea	+	Tignere	+	767
    Edea	+	Wum	+	436
    Edea	+	Yabassi	+	104
    Edea	+	Yagoua	+	1398
    Edea	+	Yaounde	+	173
    Edea	+	Yokadouma	+	762
    Eseka	+	Fontem	+	384
    Eseka	+	Foumban	+	336
    Eseka	+	Fundong	+	440
    Eseka	+	Garoua	+	1058
    Eseka	+	Guider	+	1162
    Eseka	+	Kaele	+	1237
    Eseka	+	Kousseri	+	1524
    Eseka	+	Kribi	+	169
    Eseka	+	Kumba	+	308
    Eseka	+	Kumbo	+	416
    Eseka	+	Limbe	+	246
    Eseka	+	Mamfe	+	456
    Eseka	+	Maroua	+	1259
    Eseka	+	Mbalmayo	+	121
    Eseka	+	Mbengwi	+	397
    Eseka	+	Mbouda	+	323
    Eseka	+	Meiganga	+	797
    Eseka	+	Mfou	+	144
    Eseka	+	Mokolo	+	1266
    Eseka	+	Monatele	+	133
    Eseka	+	Mora	+	1320
    Eseka	+	Mundemba	+	394
    Eseka	+	Nanga-Eboko	+	283
    Eseka	+	Ndop	+	383
    Eseka	+	Ngaoundere	+	784
    Eseka	+	Ngoumou	+	83
    Eseka	+	Nkambe	+	485
    Eseka	+	Nkongsamba	+	308
    Eseka	+	Ntui	+	201
    Eseka	+	Poli	+	997
    Eseka	+	Sangmelima	+	242
    Eseka	+	Tchollire	+	1000
    Eseka	+	Tibati	+	563
    Eseka	+	Tignere	+	715
    Eseka	+	Wum	+	450
    Eseka	+	Yabassi	+	211
    Eseka	+	Yagoua	+	1346
    Eseka	+	Yaounde	+	121
    Eseka	+	Yokadouma	+	710
    Fontem	+	Foumban	+	159
    Fontem	+	Fundong	+	195
    Fontem	+	Garoua	+	975
    Fontem	+	Guider	+	1078
    Fontem	+	Kaele	+	1154
    Fontem	+	Kousseri	+	1440
    Fontem	+	Kribi	+	424
    Fontem	+	Kumba	+	178
    Fontem	+	Kumbo	+	206
    Fontem	+	Limbe	+	269
    Fontem	+	Mamfe	+	88
    Fontem	+	Maroua	+	1176
    Fontem	+	Mbalmayo	+	430
    Fontem	+	Mbengwi	+	152
    Fontem	+	Mbouda	+	78
    Fontem	+	Meiganga	+	732
    Fontem	+	Mfou	+	412
    Fontem	+	Mokolo	+	1183
    Fontem	+	Monatele	+	325
    Fontem	+	Mora	+	1236
    Fontem	+	Mundemba	+	186
    Fontem	+	Nanga-Eboko	+	462
    Fontem	+	Ndop	+	138
    Fontem	+	Ngaoundere	+	701
    Fontem	+	Ngoumou	+	432
    Fontem	+	Nkambe	+	274
    Fontem	+	Nkongsamba	+	114
    Fontem	+	Ntui	+	336
    Fontem	+	Poli	+	914
    Fontem	+	Sangmelima	+	542
    Fontem	+	Tchollire	+	917
    Fontem	+	Tibati	+	480
    Fontem	+	Tignere	+	632
    Fontem	+	Wum	+	205
    Fontem	+	Yabassi	+	210
    Fontem	+	Yagoua	+	1263
    Fontem	+	Yokadouma	+	910
    Fontem	+	Yaounde	+	383
    Foumban	+	Fundong	+	168
    Foumban	+	Garoua	+	816
    Foumban	+	Guider	+	920
    Foumban	+	Kaele	+	995
    Foumban	+	Kousseri	+	1282
    Foumban	+	Kribi	+	456
    Foumban	+	Kumba	+	261
    Foumban	+	Kumbo	+	88
    Foumban	+	Limbe	+	352
    Foumban	+	Mamfe	+	247
    Foumban	+	Maroua	+	1017
    Foumban	+	Mbalmayo	+	348
    Foumban	+	Mbengwi	+	168
    Foumban	+	Mbouda	+	95
    Foumban	+	Meiganga	+	573
    Foumban	+	Mfou	+	330
    Foumban	+	Mokolo	+	1024
    Foumban	+	Monatele	+	243
    Foumban	+	Mora	+	1078
    Foumban	+	Mundemba	+	345
    Foumban	+	Nanga-Eboko	+	380
    Foumban	+	Ndop	+	115
    Foumban	+	Ngaoundere	+	542
    Foumban	+	Ngoumou	+	350
    Foumban	+	Nkambe	+	157
    Foumban	+	Nkongsamba	+	181
    Foumban	+	Ntui	+	254
    Foumban	+	Poli	+	755
    Foumban	+	Sangmelima	+	460
    Foumban	+	Tchollire	+	758
    Foumban	+	Tibati	+	321
    Foumban	+	Tignere	+	473
    Foumban	+	Wum	+	211
    Foumban	+	Yabassi	+	273
    Foumban	+	Yagoua	+	1104
    Foumban	+	Yaounde	+	301
    Foumban	+	Yokadouma	+	828
    Fundong	+	Garoua	+	937
    Fundong	+	Guider	+	1040
    Fundong	+	Kaele	+	1116
    Fundong	+	Kousseri	+	1402
    Fundong	+	Kribi	+	537
    Fundong	+	Kumba	+	306
    Fundong	+	Kumbo	+	79
    Fundong	+	Limbe	+	396
    Fundong	+	Mamfe	+	211
    Fundong	+	Maroua	+	1137
    Fundong	+	Mbalmayo	+	486
    Fundong	+	Mbengwi	+	93
    Fundong	+	Mbouda	+	116
    Fundong	+	Meiganga	+	694
    Fundong	+	Mfou	+	468
    Fundong	+	Mokolo	+	1145
    Fundong	+	Monatele	+	380
    Fundong	+	Mora	+	1198
    Fundong	+	Mundemba	+	356
    Fundong	+	Nanga-Eboko	+	517
    Fundong	+	Ndop	+	81
    Fundong	+	Ngaoundere	+	663
    Fundong	+	Ngoumou	+	487
    Fundong	+	Nkambe	+	132
    Fundong	+	Nkongsamba	+	226
    Fundong	+	Ntui	+	392
    Fundong	+	Poli	+	876
    Fundong	+	Sangmelima	+	597
    Fundong	+	Tchollire	+	879
    Fundong	+	Tibati	+	442
    Fundong	+	Tignere	+	594
    Fundong	+	Wum	+	47
    Fundong	+	Yabassi	+	322
    Fundong	+	Yagoua	+	1225
    Fundong	+	Yaounde	+	439
    Fundong	+	Yokadouma	+	966
    Garoua	+	Guider	+	104
    Garoua	+	Kaele	+	179
    Garoua	+	Kousseri	+	466
    Garoua	+	Kribi	+	1220
    Garoua	+	Kumba	+	1077
    Garoua	+	Kumbo	+	857
    Garoua	+	Limbe	+	1167
    Garoua	+	Mamfe	+	1062
    Garoua	+	Maroua	+	201
    Garoua	+	Mbalmayo	+	1003
    Garoua	+	Mbengwi	+	982
    Garoua	+	Mbouda	+	910
    Garoua	+	Meiganga	+	432
    Garoua	+	Mfou	+	985
    Garoua	+	Mokolo	+	208
    Garoua	+	Monatele	+	950
    Garoua	+	Mora	+	262
    Garoua	+	Mundemba	+	1161
    Garoua	+	Nanga-Eboko	+	947
    Garoua	+	Ndop	+	925
    Garoua	+	Ngaoundere	+	276
    Garoua	+	Ngoumou	+	1004
    Garoua	+	Nkambe	+	869
    Garoua	+	Nkongsamba	+	997
    Garoua	+	Ntui	+	875
    Garoua	+	Poli	+	137
    Garoua	+	Sangmelima	+	1114
    Garoua	+	Tchollire	+	195
    Garoua	+	Tibati	+	495
    Garoua	+	Tignere	+	406
    Garoua	+	Wum	+	978
    Garoua	+	Yabassi	+	1089
    Garoua	+	Yagoua	+	288
    Garoua	+	Yaounde	+	956
    Garoua	+	Yokadouma	+	943
    Guider	+	Kaele	+	80
    Guider	+	Kousseri	+	362
    Guider	+	Kribi	+	1324
    Guider	+	Kumba	+	1181
    Guider	+	Kumbo	+	961
    Guider	+	Limbe	+	1271
    Guider	+	Mamfe	+	1166
    Guider	+	Maroua	+	97
    Guider	+	Mbalmayo	+	1106
    Guider	+	Mbengwi	+	1086
    Guider	+	Mbouda	+	1014
    Guider	+	Meiganga	+	536
    Guider	+	Mfou	+	1088
    Guider	+	Mokolo	+	105
    Guider	+	Monatele	+	1054
    Guider	+	Mora	+	158
    Guider	+	Mundemba	+	1265
    Guider	+	Nanga-Eboko	+	1050
    Guider	+	Ndop	+	1029
    Guider	+	Ngaoundere	+	380
    Guider	+	Ngoumou	+	1108
    Guider	+	Nkambe	+	973
    Guider	+	Nkongsamba	+	1101
    Guider	+	Ntui	+	978
    Guider	+	Poli	+	241
    Guider	+	Sangmelima	+	1218
    Guider	+	Tchollire	+	285
    Guider	+	Tibati	+	598
    Guider	+	Tignere	+	510
    Guider	+	Wum	+	1082
    Guider	+	Yabassi	+	1192
    Guider	+	Yagoua	+	189
    Guider	+	Yaounde	+	1059
    Guider	+	Yokadouma	+	1046
    Kaele	+	Kousseri	+	329
    Kaele	+	Kribi	+	1400
    Kaele	+	Kumba	+	1257
    Kaele	+	Kumbo	+	1037
    Kaele	+	Limbe	+	1347
    Kaele	+	Mamfe	+	1242
    Kaele	+	Maroua	+	72
    Kaele	+	Mbalmayo	+	1182
    Kaele	+	Mbengwi	+	1162
    Kaele	+	Mbouda	+	1090
    Kaele	+	Meiganga	+	612
    Kaele	+	Mfou	+	1164
    Kaele	+	Mokolo	+	147
    Kaele	+	Monatele	+	1129
    Kaele	+	Mora	+	133
    Kaele	+	Mundemba	+	1340
    Kaele	+	Nanga-Eboko	+	1126
    Kaele	+	Ndop	+	1105
    Kaele	+	Ngaoundere	+	456
    Kaele	+	Ngoumou	+	1184
    Kaele	+	Nkambe	+	1048
    Kaele	+	Nkongsamba	+	1176
    Kaele	+	Ntui	+	1054
    Kaele	+	Poli	+	316
    Kaele	+	Sangmelima	+	1294
    Kaele	+	Tchollire	+	361
    Kaele	+	Tibati	+	674
    Kaele	+	Tignere	+	585
    Kaele	+	Wum	+	1158
    Kaele	+	Yabassi	+	1268
    Kaele	+	Yagoua	+	109
    Kaele	+	Yaounde	+	1135
    Kaele	+	Yokadouma	+	1122
    Kousseri	+	Kribi	+	1686
    Kousseri	+	Kumba	+	1543
    Kousseri	+	Kumbo	+	1323
    Kousseri	+	Limbe	+	1633
    Kousseri	+	Mamfe	+	1528
    Kousseri	+	Maroua	+	265
    Kousseri	+	Mbalmayo	+	1468
    Kousseri	+	Mbengwi	+	1448
    Kousseri	+	Mbouda	+	1376
    Kousseri	+	Meiganga	+	898
    Kousseri	+	Mfou	+	1450
    Kousseri	+	Mokolo	+	276
    Kousseri	+	Monatele	+	1416
    Kousseri	+	Mora	+	209
    Kousseri	+	Mundemba	+	1627
    Kousseri	+	Nanga-Eboko	+	1412
    Kousseri	+	Ndop	+	1391
    Kousseri	+	Ngaoundere	+	742
    Kousseri	+	Ngoumou	+	1470
    Kousseri	+	Nkambe	+	1335
    Kousseri	+	Nkongsamba	+	1463
    Kousseri	+	Ntui	+	1340
    Kousseri	+	Poli	+	603
    Kousseri	+	Sangmelima	+	1580
    Kousseri	+	Tchollire	+	647
    Kousseri	+	Tibati	+	960
    Kousseri	+	Tignere	+	872
    Kousseri	+	Wum	+	1444
    Kousseri	+	Yabassi	+	1554
    Kousseri	+	Yagoua	+	241
    Kousseri	+	Yaounde	+	1421
    Kousseri	+	Yokadouma	+	1408
    Kribi	+	Kumba	+	311
    Kribi	+	Kumbo	+	531
    Kribi	+	Limbe	+	249
    Kribi	+	Mamfe	+	459
    Kribi	+	Maroua	+	1421
    Kribi	+	Mbalmayo	+	244
    Kribi	+	Mbengwi	+	494
    Kribi	+	Mbouda	+	420
    Kribi	+	Meiganga	+	949
    Kribi	+	Mfou	+	288
    Kribi	+	Mokolo	+	1429
    Kribi	+	Monatele	+	295
    Kribi	+	Mora	+	1482
    Kribi	+	Mundemba	+	397
    Kribi	+	Nanga-Eboko	+	444
    Kribi	+	Ndop	+	480
    Kribi	+	Ngaoundere	+	946
    Kribi	+	Ngoumou	+	232
    Kribi	+	Nkambe	+	600
    Kribi	+	Nkongsamba	+	311
    Kribi	+	Ntui	+	361
    Kribi	+	Poli	+	1160
    Kribi	+	Sangmelima	+	290
    Kribi	+	Tchollire	+	1163
    Kribi	+	Tibati	+	726
    Kribi	+	Tignere	+	877
    Kribi	+	Wum	+	547
    Kribi	+	Yabassi	+	214
    Kribi	+	Yagoua	+	1508
    Kribi	+	Yaounde	+	280
    Kribi	+	Yokadouma	+	862
    Kumba	+	Kumbo	+	317
    Kumba	+	Limbe	+	91
    Kumba	+	Mamfe	+	159
    Kumba	+	Maroua	+	1278
    Kumba	+	Mbalmayo	+	412
    Kumba	+	Mbengwi	+	264
    Kumba	+	Mbouda	+	190
    Kumba	+	Meiganga	+	834
    Kumba	+	Mfou	+	396
    Kumba	+	Mokolo	+	1286
    Kumba	+	Monatele	+	370
    Kumba	+	Mora	+	1339
    Kumba	+	Mundemba	+	113
    Kumba	+	Nanga-Eboko	+	507
    Kumba	+	Ndop	+	249
    Kumba	+	Ngaoundere	+	803
    Kumba	+	Ngoumou	+	390
    Kumba	+	Nkambe	+	386
    Kumba	+	Nkongsamba	+	80
    Kumba	+	Ntui	+	381
    Kumba	+	Poli	+	1017
    Kumba	+	Sangmelima	+	526
    Kumba	+	Tchollire	+	1019
    Kumba	+	Tibati	+	583
    Kumba	+	Tignere	+	734
    Kumba	+	Wum	+	316
    Kumba	+	Yabassi	+	97
    Kumba	+	Yagoua	+	1365
    Kumba	+	Yaounde	+	374
    Kumba	+	Yokadouma	+	955
    Kumbo	+	Limbe	+	408
    Kumbo	+	Mamfe	+	242
    Kumbo	+	Maroua	+	1058
    Kumbo	+	Mbalmayo	+	430
    Kumbo	+	Mbengwi	+	125
    Kumbo	+	Mbouda	+	128
    Kumbo	+	Meiganga	+	614
    Kumbo	+	Mfou	+	411
    Kumbo	+	Mokolo	+	1066
    Kumbo	+	Monatele	+	325
    Kumbo	+	Mora	+	1119
    Kumbo	+	Mundemba	+	388
    Kumbo	+	Nanga-Eboko	+	461
    Kumbo	+	Ndop	+	68
    Kumbo	+	Ngaoundere	+	583
    Kumbo	+	Ngoumou	+	431
    Kumbo	+	Nkambe	+	68
    Kumbo	+	Nkongsamba	+	237
    Kumbo	+	Ntui	+	336
    Kumbo	+	Poli	+	797
    Kumbo	+	Sangmelima	+	541
    Kumbo	+	Tchollire	+	800
    Kumbo	+	Tibati	+	363
    Kumbo	+	Tignere	+	514
    Kumbo	+	Wum	+	127
    Kumbo	+	Yabassi	+	334
    Kumbo	+	Yagoua	+	1145
    Kumbo	+	Yaounde	+	383
    Kumbo	+	Yokadouma	+	909
    Limbe	+	Mamfe	+	250
    Limbe	+	Maroua	+	1368
    Limbe	+	Mbalmayo	+	350
    Limbe	+	Mbengwi	+	354
    Limbe	+	Mbouda	+	280
    Limbe	+	Meiganga	+	924
    Limbe	+	Mfou	+	334
    Limbe	+	Mokolo	+	1376
    Limbe	+	Monatele	+	323
    Limbe	+	Mora	+	1429
    Limbe	+	Mundemba	+	171
    Limbe	+	Nanga-Eboko	+	473
    Limbe	+	Ndop	+	340
    Limbe	+	Ngaoundere	+	893
    Limbe	+	Ngoumou	+	328
    Limbe	+	Nkambe	+	476
    Limbe	+	Nkongsamba	+	170
    Limbe	+	Ntui	+	390
    Limbe	+	Poli	+	1107
    Limbe	+	Sangmelima	+	463
    Limbe	+	Tchollire	+	1110
    Limbe	+	Tibati	+	673
    Limbe	+	Tignere	+	824
    Limbe	+	Wum	+	407
    Limbe	+	Yabassi	+	167
    Limbe	+	Yagoua	+	1455
    Limbe	+	Yaounde	+	311
    Limbe	+	Yokadouma	+	900
    Mamfe	+	Maroua	+	1263
    Mamfe	+	Mbalmayo	+	518
    Mamfe	+	Mbengwi	+	128
    Mamfe	+	Mbouda	+	166
    Mamfe	+	Meiganga	+	819
    Mamfe	+	Mfou	+	499
    Mamfe	+	Mokolo	+	1271
    Mamfe	+	Monatele	+	412
    Mamfe	+	Mora	+	1324
    Mamfe	+	Mundemba	+	167
    Mamfe	+	Nanga-Eboko	+	549
    Mamfe	+	Ndop	+	177
    Mamfe	+	Ngaoundere	+	788
    Mamfe	+	Ngoumou	+	519
    Mamfe	+	Nkambe	+	311
    Mamfe	+	Nkongsamba	+	201
    Mamfe	+	Ntui	+	424
    Mamfe	+	Poli	+	1002
    Mamfe	+	Sangmelima	+	629
    Mamfe	+	Tchollire	+	1005
    Mamfe	+	Tibati	+	568
    Mamfe	+	Tignere	+	719
    Mamfe	+	Wum	+	214
    Mamfe	+	Yabassi	+	245
    Mamfe	+	Yagoua	+	1350
    Mamfe	+	Yaounde	+	470
    Mamfe	+	Yokadouma	+	997
    Maroua	+	Mbalmayo	+	1203
    Maroua	+	Mbengwi	+	1183
    Maroua	+	Mbouda	+	1111
    Maroua	+	Meiganga	+	633
    Maroua	+	Mfou	+	1185
    Maroua	+	Mokolo	+	75
    Maroua	+	Monatele	+	1151
    Maroua	+	Mora	+	61
    Maroua	+	Mundemba	+	1362
    Maroua	+	Nanga-Eboko	+	1148
    Maroua	+	Ndop	+	1126
    Maroua	+	Ngaoundere	+	477
    Maroua	+	Ngoumou	+	1205
    Maroua	+	Nkambe	+	1070
    Maroua	+	Nkongsamba	+	1198
    Maroua	+	Ntui	+	1075
    Maroua	+	Poli	+	338
    Maroua	+	Sangmelima	+	1315
    Maroua	+	Tchollire	+	382
    Maroua	+	Tibati	+	696
    Maroua	+	Tignere	+	607
    Maroua	+	Wum	+	1179
    Maroua	+	Yabassi	+	1290
    Maroua	+	Yagoua	+	120
    Maroua	+	Yaounde	+	1156
    Maroua	+	Yokadouma	+	1144
    Mbalmayo	+	Mbengwi	+	443
    Mbalmayo	+	Mbouda	+	369
    Mbalmayo	+	Meiganga	+	705
    Mbalmayo	+	Mfou	+	44
    Mbalmayo	+	Mokolo	+	1211
    Mbalmayo	+	Monatele	+	123
    Mbalmayo	+	Mora	+	1264
    Mbalmayo	+	Mundemba	+	498
    Mbalmayo	+	Nanga-Eboko	+	210
    Mbalmayo	+	Ndop	+	414
    Mbalmayo	+	Ngaoundere	+	729
    Mbalmayo	+	Ngoumou	+	37
    Mbalmayo	+	Nkambe	+	498
    Mbalmayo	+	Nkongsamba	+	395
    Mbalmayo	+	Ntui	+	128
    Mbalmayo	+	Poli	+	942
    Mbalmayo	+	Sangmelima	+	122
    Mbalmayo	+	Tchollire	+	945
    Mbalmayo	+	Tibati	+	508
    Mbalmayo	+	Tignere	+	660
    Mbalmayo	+	Wum	+	496
    Mbalmayo	+	Yabassi	+	315
    Mbalmayo	+	Yagoua	+	1291
    Mbalmayo	+	Yaounde	+	47
    Mbalmayo	+	Yokadouma	+	619
    Mbengwi	+	Mbouda	+	74
    Mbengwi	+	Meiganga	+	739
    Mbengwi	+	Mfou	+	425
    Mbengwi	+	Mokolo	+	1191
    Mbengwi	+	Monatele	+	338
    Mbengwi	+	Mora	+	1244
    Mbengwi	+	Mundemba	+	273
    Mbengwi	+	Nanga-Eboko	+	475
    Mbengwi	+	Ndop	+	60
    Mbengwi	+	Ngaoundere	+	708
    Mbengwi	+	Ngoumou	+	445
    Mbengwi	+	Nkambe	+	193
    Mbengwi	+	Nkongsamba	+	183
    Mbengwi	+	Ntui	+	349
    Mbengwi	+	Poli	+	922
    Mbengwi	+	Sangmelima	+	555
    Mbengwi	+	Tchollire	+	925
    Mbengwi	+	Tibati	+	488
    Mbengwi	+	Tignere	+	639
    Mbengwi	+	Wum	+	97
    Mbengwi	+	Yabassi	+	280
    Mbengwi	+	Yagoua	+	1270
    Mbengwi	+	Yaounde	+	396
    Mbengwi	+	Yokadouma	+	923
    Mbouda	+	Meiganga	+	667
    Mbouda	+	Mfou	+	351
    Mbouda	+	Mokolo	+	1119
    Mbouda	+	Monatele	+	264
    Mbouda	+	Mora	+	1172
    Mbouda	+	Mundemba	+	265
    Mbouda	+	Nanga-Eboko	+	401
    Mbouda	+	Ndop	+	60
    Mbouda	+	Ngaoundere	+	636
    Mbouda	+	Ngoumou	+	371
    Mbouda	+	Nkambe	+	196
    Mbouda	+	Nkongsamba	+	110
    Mbouda	+	Ntui	+	275
    Mbouda	+	Poli	+	850
    Mbouda	+	Sangmelima	+	481
    Mbouda	+	Tchollire	+	853
    Mbouda	+	Tibati	+	416
    Mbouda	+	Tignere	+	567
    Mbouda	+	Wum	+	127
    Mbouda	+	Yabassi	+	206
    Mbouda	+	Yagoua	+	1198
    Mbouda	+	Yaounde	+	322
    Mbouda	+	Yokadouma	+	849
    Meiganga	+	Mfou	+	679
    Meiganga	+	Mokolo	+	641
    Meiganga	+	Monatele	+	687
    Meiganga	+	Mora	+	694
    Meiganga	+	Mundemba	+	918
    Meiganga	+	Nanga-Eboko	+	514
    Meiganga	+	Ndop	+	682
    Meiganga	+	Ngaoundere	+	156
    Meiganga	+	Ngoumou	+	720
    Meiganga	+	Nkambe	+	626
    Meiganga	+	Nkongsamba	+	754
    Meiganga	+	Ntui	+	632
    Meiganga	+	Poli	+	372
    Meiganga	+	Sangmelima	+	710
    Meiganga	+	Tchollire	+	375
    Meiganga	+	Tibati	+	252
    Meiganga	+	Tignere	+	288
    Meiganga	+	Wum	+	735
    Meiganga	+	Yabassi	+	846
    Meiganga	+	Yagoua	+	721
    Meiganga	+	Yaounde	+	678
    Meiganga	+	Yokadouma	+	510
    Mfou	+	Mokolo	+	1193
    Mfou	+	Monatele	+	105
    Mfou	+	Mora	+	1246
    Mfou	+	Mundemba	+	482
    Mfou	+	Nanga-Eboko	+	192
    Mfou	+	Ndop	+	396
    Mfou	+	Ngaoundere	+	711
    Mfou	+	Ngoumou	+	66
    Mfou	+	Nkambe	+	480
    Mfou	+	Nkongsamba	+	377
    Mfou	+	Ntui	+	110
    Mfou	+	Poli	+	924
    Mfou	+	Sangmelima	+	130
    Mfou	+	Tchollire	+	927
    Mfou	+	Tibati	+	490
    Mfou	+	Tignere	+	641
    Mfou	+	Wum	+	478
    Mfou	+	Yabassi	+	299
    Mfou	+	Yagoua	+	1273
    Mfou	+	Yaounde	+	29
    Mfou	+	Yokadouma	+	592
    Mokolo	+	Monatele	+	1159
    Mokolo	+	Mora	+	67
    Mokolo	+	Mundemba	+	1369
    Mokolo	+	Nanga-Eboko	+	1155
    Mokolo	+	Ndop	+	1134
    Mokolo	+	Ngaoundere	+	485
    Mokolo	+	Ngoumou	+	1213
    Mokolo	+	Nkambe	+	1077
    Mokolo	+	Nkongsamba	+	1205
    Mokolo	+	Ntui	+	1083
    Mokolo	+	Poli	+	345
    Mokolo	+	Sangmelima	+	1323
    Mokolo	+	Tchollire	+	390
    Mokolo	+	Tibati	+	703
    Mokolo	+	Tignere	+	614
    Mokolo	+	Wum	+	1187
    Mokolo	+	Yabassi	+	1297
    Mokolo	+	Yagoua	+	196
    Mokolo	+	Yaounde	+	1164
    Mokolo	+	Yokadouma	+	1151
    Monatele	+	Mora	+	1213
    Monatele	+	Mundemba	+	471
    Monatele	+	Nanga-Eboko	+	174
    Monatele	+	Ndop	+	310
    Monatele	+	Ngaoundere	+	676
    Monatele	+	Ngoumou	+	124
    Monatele	+	Nkambe	+	394
    Monatele	+	Nkongsamba	+	291
    Monatele	+	Ntui	+	77
    Monatele	+	Poli	+	891
    Monatele	+	Sangmelima	+	234
    Monatele	+	Tchollire	+	894
    Monatele	+	Tibati	+	457
    Monatele	+	Tignere	+	608
    Monatele	+	Wum	+	392
    Monatele	+	Yabassi	+	282
    Monatele	+	Yagoua	+	1239
    Monatele	+	Yaounde	+	76
    Monatele	+	Yokadouma	+	622
    Mora	+	Mundemba	+	1423
    Mora	+	Nanga-Eboko	+	1208
    Mora	+	Ndop	+	1187
    Mora	+	Ngaoundere	+	538
    Mora	+	Ngoumou	+	1266
    Mora	+	Nkambe	+	1131
    Mora	+	Nkongsamba	+	1259
    Mora	+	Ntui	+	1136
    Mora	+	Poli	+	399
    Mora	+	Sangmelima	+	1376
    Mora	+	Tchollire	+	443
    Mora	+	Tibati	+	756
    Mora	+	Tignere	+	668
    Mora	+	Wum	+	1240
    Mora	+	Yabassi	+	1350
    Mora	+	Yagoua	+	181
    Mora	+	Yaounde	+	1217
    Mora	+	Yokadouma	+	1205
    Mundemba	+	Nanga-Eboko	+	619
    Mundemba	+	Ndop	+	323
    Mundemba	+	Ngaoundere	+	887
    Mundemba	+	Ngoumou	+	476
    Mundemba	+	Nkambe	+	456
    Mundemba	+	Nkongsamba	+	193
    Mundemba	+	Ntui	+	494
    Mundemba	+	Poli	+	1100
    Mundemba	+	Sangmelima	+	612
    Mundemba	+	Tchollire	+	1103
    Mundemba	+	Tibati	+	667
    Mundemba	+	Tignere	+	818
    Mundemba	+	Wum	+	359
    Mundemba	+	Yabassi	+	209
    Mundemba	+	Yagoua	+	1449
    Mundemba	+	Yaounde	+	460
    Mundemba	+	Yokadouma	+	1048
    Nanga-Eboko	+	Ndop	+	446
    Nanga-Eboko	+	Ngaoundere	+	670
    Nanga-Eboko	+	Ngoumou	+	212
    Nanga-Eboko	+	Nkambe	+	530
    Nanga-Eboko	+	Nkongsamba	+	426
    Nanga-Eboko	+	Ntui	+	125
    Nanga-Eboko	+	Poli	+	886
    Nanga-Eboko	+	Sangmelima	+	267
    Nanga-Eboko	+	Tchollire	+	889
    Nanga-Eboko	+	Tibati	+	505
    Nanga-Eboko	+	Tignere	+	657
    Nanga-Eboko	+	Wum	+	527
    Nanga-Eboko	+	Yabassi	+	425
    Nanga-Eboko	+	Yagoua	+	1235
    Nanga-Eboko	+	Yaounde	+	163
    Nanga-Eboko	+	Yokadouma	+	448
    Ndop	+	Ngaoundere	+	651
    Ndop	+	Ngoumou	+	416
    Ndop	+	Nkambe	+	137
    Ndop	+	Nkongsamba	+	169
    Ndop	+	Ntui	+	320
    Ndop	+	Poli	+	865
    Ndop	+	Sangmelima	+	526
    Ndop	+	Tchollire	+	868
    Ndop	+	Tibati	+	431
    Ndop	+	Tignere	+	582
    Ndop	+	Wum	+	96
    Ndop	+	Yabassi	+	266
    Ndop	+	Yagoua	+	1213
    Ndop	+	Yaounde	+	367
    Ndop	+	Yokadouma	+	894
    Ngaoundere	+	Ngoumou	+	730
    Ngaoundere	+	Nkambe	+	595
    Ngaoundere	+	Nkongsamba	+	723
    Ngaoundere	+	Ntui	+	601
    Ngaoundere	+	Poli	+	216
    Ngaoundere	+	Sangmelima	+	841
    Ngaoundere	+	Tchollire	+	219
    Ngaoundere	+	Tibati	+	221
    Ngaoundere	+	Tignere	+	132
    Ngaoundere	+	Wum	+	704
    Ngaoundere	+	Yabassi	+	815
    Ngaoundere	+	Yagoua	+	564
    Ngaoundere	+	Yaounde	+	682
    Ngaoundere	+	Yokadouma	+	667
    Ngoumou	+	Nkambe	+	500
    Ngoumou	+	Nkongsamba	+	390
    Ngoumou	+	Ntui	+	130
    Ngoumou	+	Poli	+	944
    Ngoumou	+	Sangmelima	+	159
    Ngoumou	+	Tchollire	+	947
    Ngoumou	+	Tibati	+	509
    Ngoumou	+	Tignere	+	661
    Ngoumou	+	Wum	+	497
    Ngoumou	+	Yabassi	+	293
    Ngoumou	+	Yagoua	+	1292
    Ngoumou	+	Yaounde	+	49
    Ngoumou	+	Yokadouma	+	633
    Nkambe	+	Nkongsamba	+	306
    Nkambe	+	Ntui	+	404
    Nkambe	+	Poli	+	808
    Nkambe	+	Sangmelima	+	610
    Nkambe	+	Tchollire	+	811
    Nkambe	+	Tibati	+	374
    Nkambe	+	Tignere	+	526
    Nkambe	+	Wum	+	109
    Nkambe	+	Yabassi	+	402
    Nkambe	+	Yagoua	+	1157
    Nkambe	+	Yaounde	+	451
    Nkambe	+	Yokadouma	+	978
    Nkongsamba	+	Ntui	+	301
    Nkongsamba	+	Poli	+	936
    Nkongsamba	+	Sangmelima	+	507
    Nkongsamba	+	Tchollire	+	939
    Nkongsamba	+	Tibati	+	503
    Nkongsamba	+	Tignere	+	654
    Nkongsamba	+	Wum	+	236
    Nkongsamba	+	Yabassi	+	96
    Nkongsamba	+	Yagoua	+	1285
    Nkongsamba	+	Yaounde	+	348
    Nkongsamba	+	Yokadouma	+	875
    Ntui	+	Poli	+	814
    Ntui	+	Sangmelima	+	240
    Ntui	+	Tchollire	+	817
    Ntui	+	Tibati	+	380
    Ntui	+	Tignere	+	532
    Ntui	+	Wum	+	402
    Ntui	+	Yabassi	+	300
    Ntui	+	Yagoua	+	1163
    Ntui	+	Yaounde	+	81
    Ntui	+	Yokadouma	+	574
    Poli	+	Sangmelima	+	1054
    Poli	+	Tchollire	+	134
    Poli	+	Tibati	+	434
    Poli	+	Tignere	+	345
    Poli	+	Wum	+	918
    Poli	+	Yabassi	+	1028
    Poli	+	Yagoua	+	425
    Poli	+	Yaounde	+	895
    Poli	+	Yokadouma	+	882
    Sangmelima	+	Tchollire	+	1057
    Sangmelima	+	Tibati	+	620
    Sangmelima	+	Tignere	+	771
    Sangmelima	+	Wum	+	608
    Sangmelima	+	Yabassi	+	429
    Sangmelima	+	Yagoua	+	1403
    Sangmelima	+	Yaounde	+	159
    Sangmelima	+	Yokadouma	+	623
    Tchollire	+	Tibati	+	437
    Tchollire	+	Tignere	+	348
    Tchollire	+	Wum	+	921
    Tchollire	+	Yabassi	+	1031
    Tchollire	+	Yagoua	+	470
    Tchollire	+	Yaounde	+	898
    Tchollire	+	Yokadouma	+	885
    Tibati	+	Tignere	+	152
    Tibati	+	Wum	+	484
    Tibati	+	Yabassi	+	594
    Tibati	+	Yagoua	+	783
    Tibati	+	Yaounde	+	461
    Tibati	+	Yokadouma	+	742
    Tignere	+	Wum	+	635
    Tignere	+	Yabassi	+	746
    Tignere	+	Yagoua	+	694
    Tignere	+	Yaounde	+	613
    Tignere	+	Yokadouma	+	799
    Wum	+	Yabassi	+	333
    Wum	+	Yagoua	+	1266
    Wum	+	Yaounde	+	449
    Wum	+	Yokadouma	+	976
    Yabassi	+	Yagoua	+	1377
    Yabassi	+	Yaounde	+	277
    Yabassi	+	Yokadouma	+	865
    Yagoua	+	Yaounde	+	1244
    Yagoua	+	Yokadouma	+	1231
    Yaounde	+	Yokadouma	+	595
    """
}