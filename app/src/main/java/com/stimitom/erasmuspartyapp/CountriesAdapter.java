package com.stimitom.erasmuspartyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CountriesAdapter extends RecyclerView.Adapter<CountriesAdapter.CountriesViewHolder> {
    private ArrayList<String> nationalities;

    public CountriesAdapter(ArrayList<String> nationalities) {
        this.nationalities = nationalities;
    }

    @NonNull
    @Override
    public CountriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View nationality = layoutInflater.inflate(R.layout.nationality, parent, false);
        return new CountriesViewHolder(nationality);
    }

    @Override
    public void onBindViewHolder(@NonNull CountriesViewHolder holder, int position) {
        String country = nationalities.get(position);
        holder.nationalityTextView.setText(country);
        setFlagForCountry(country, holder);
    }

    @Override
    public int getItemCount() {
        return nationalities.size();
    }

    public static class CountriesViewHolder extends RecyclerView.ViewHolder {
        private TextView nationalityTextView;
        private ImageView flagView;


        public CountriesViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nationalityTextView = (TextView) itemView.findViewById(R.id.text_view_nationality);
            this.flagView = (ImageView) itemView.findViewById(R.id.flag_image_view);
        }
    }

    public void setFlagForCountry(String country, CountriesViewHolder holder) {
        switch (country) {
            case "Afghanistan":
                holder.flagView.setImageResource(R.drawable.flag_afghanistan);
                break;
            case "Aland Islands":
                holder.flagView.setImageResource(R.drawable.flag_aland_islands);
                break;
            case "Albania":
                holder.flagView.setImageResource(R.drawable.flag_albania);
                break;
            case "Algeria":
                holder.flagView.setImageResource(R.drawable.flag_algeria);
                break;
            case "American Samoa":
                holder.flagView.setImageResource(R.drawable.flag_american_samoa);
                break;
            case "Andorra":
                holder.flagView.setImageResource(R.drawable.flag_andorra);
                break;
            case "Angola":
                holder.flagView.setImageResource(R.drawable.flag_angola);
                break;
            case "Anguilla":
                holder.flagView.setImageResource(R.drawable.flag_anguilla);
                break;
            case "Antigua and Barbuda":
                holder.flagView.setImageResource(R.drawable.flag_antigua_and_barbuda);
                break;
            case "Argentina":
                holder.flagView.setImageResource(R.drawable.flag_argentina);
                break;
            case "Armenia":
                holder.flagView.setImageResource(R.drawable.flag_armenia);
                break;
            case "Aruba":
                holder.flagView.setImageResource(R.drawable.flag_aruba);
                break;
            case "Australia":
                holder.flagView.setImageResource(R.drawable.flag_australia);
                break;
            case "Austria":
                holder.flagView.setImageResource(R.drawable.flag_austria);
                break;
            case "Azerbaijan":
                holder.flagView.setImageResource(R.drawable.flag_azerbaijan);
                break;
            case "Bahamas":
                holder.flagView.setImageResource(R.drawable.flag_bahamas);
                break;
            case "Bahrain":
                holder.flagView.setImageResource(R.drawable.flag_bahrain);
                break;
            case "Bangladesh":
                holder.flagView.setImageResource(R.drawable.flag_bangladesh);
                break;
            case "Barbados":
                holder.flagView.setImageResource(R.drawable.flag_barbados);
                break;
            case "Belarus":
                holder.flagView.setImageResource(R.drawable.flag_belarus);
                break;
            case "Belgium":
                holder.flagView.setImageResource(R.drawable.flag_belgium);
                break;
            case "Belize":
                holder.flagView.setImageResource(R.drawable.flag_belize);
                break;
            case "Benin":
                holder.flagView.setImageResource(R.drawable.flag_benin);
                break;
            case "Bermuda":
                holder.flagView.setImageResource(R.drawable.flag_bermuda);
                break;
            case "Bhutan":
                holder.flagView.setImageResource(R.drawable.flag_bhutan);
                break;
            case "Bolivia":
                holder.flagView.setImageResource(R.drawable.flag_bolivia);
                break;
            case "Bosnia_and_herzegovina":
                holder.flagView.setImageResource(R.drawable.flag_bosnia_and_herzegovina);
                break;
            case "Botswana":
                holder.flagView.setImageResource(R.drawable.flag_botswana);
                break;
            case "Brazil":
                holder.flagView.setImageResource(R.drawable.flag_brazil);
                break;
            case "British Indian Ocean Territory":
                holder.flagView.setImageResource(R.drawable.flag_british_indian_ocean_territory);
                break;
            case "Virgin Islands (British)":
                holder.flagView.setImageResource(R.drawable.flag_british_virgin_islands);
                break;
            case "Brunei Darussalam":
                holder.flagView.setImageResource(R.drawable.flag_brunei);
                break;
            case "Bulgaria":
                holder.flagView.setImageResource(R.drawable.flag_bulgaria);
                break;
            case "Burkina Faso":
                holder.flagView.setImageResource(R.drawable.flag_burkina_faso);
                break;
            case "Burundi":
                holder.flagView.setImageResource(R.drawable.flag_burundi);
                break;
            case "Cambodia":
                holder.flagView.setImageResource(R.drawable.flag_cambodia);
                break;
            case "Cameroon":
                holder.flagView.setImageResource(R.drawable.flag_cameroon);
                break;
            case "Canada":
                holder.flagView.setImageResource(R.drawable.flag_canada);
                break;
            case "Cape Verde":
                holder.flagView.setImageResource(R.drawable.flag_cape_verde);
                break;
            case "Cayman Islands":
                holder.flagView.setImageResource(R.drawable.flag_cayman_islands);
                break;
            case "Central African Republic":
                holder.flagView.setImageResource(R.drawable.flag_central_african_republic);
                break;
            case "Chad":
                holder.flagView.setImageResource(R.drawable.flag_chad);
                break;
            case "Chile":
                holder.flagView.setImageResource(R.drawable.flag_chile);
                break;
            case "China":
                holder.flagView.setImageResource(R.drawable.flag_china);
                break;
            case "Christmas Island":
                holder.flagView.setImageResource(R.drawable.flag_christmas_island);
                break;
            case "Cocos (Keeling) Islands":
                holder.flagView.setImageResource(R.drawable.flag_cocos_island);
                break;
            case "Colombia":
                holder.flagView.setImageResource(R.drawable.flag_colombia);
                break;
            case "Comoros":
                holder.flagView.setImageResource(R.drawable.flag_comoros);
                break;
            case "Cook Islands":
                holder.flagView.setImageResource(R.drawable.flag_cook_islands);
                break;
            case "Costa Rica":
                holder.flagView.setImageResource(R.drawable.flag_costa_rica);
                break;
            case "Croatia":
                holder.flagView.setImageResource(R.drawable.flag_croatia);
                break;
            case "Cuba":
                holder.flagView.setImageResource(R.drawable.flag_cuba);
                break;
            case "Curacao":
                holder.flagView.setImageResource(R.drawable.flag_curacao);
                break;
            case "Cyprus":
                holder.flagView.setImageResource(R.drawable.flag_cyprus);
                break;
            case "Czech Republic":
                holder.flagView.setImageResource(R.drawable.flag_czech_republic);
                break;
            case "Congo":
                holder.flagView.setImageResource(R.drawable.flag_democratic_republic_of_congo);
                break;
            case "Denmark":
                holder.flagView.setImageResource(R.drawable.flag_denmark);
                break;
            case "Djibouti":
                holder.flagView.setImageResource(R.drawable.flag_djibouti);
                break;
            case "Dominica":
                holder.flagView.setImageResource(R.drawable.flag_dominica);
                break;
            case "Dominican Republic":
                holder.flagView.setImageResource(R.drawable.flag_dominican_republic);
                break;
            case "Ecuador":
                holder.flagView.setImageResource(R.drawable.flag_ecuador);
                break;
            case "Egypt":
                holder.flagView.setImageResource(R.drawable.flag_egypt);
                break;
            case "England":
                holder.flagView.setImageResource(R.drawable.flag_england);
                break;
            case "Equatorial Guinea":
                holder.flagView.setImageResource(R.drawable.flag_equatorial_guinea);
                break;
            case "Eritrea":
                holder.flagView.setImageResource(R.drawable.flag_eritrea);
                break;
            case "Estonia":
                holder.flagView.setImageResource(R.drawable.flag_estonia);
                break;
            case "Ethiopia":
                holder.flagView.setImageResource(R.drawable.flag_ethiopia);
                break;
            case "Falkland Islands":
                holder.flagView.setImageResource(R.drawable.flag_falkland_islands);
                break;
            case "Faroe Islands":
                holder.flagView.setImageResource(R.drawable.flag_faroe_islands);
                break;
            case "Fiji":
                holder.flagView.setImageResource(R.drawable.flag_fiji);
                break;
            case "Finland":
                holder.flagView.setImageResource(R.drawable.flag_finland);
                break;
            case "France":
                holder.flagView.setImageResource(R.drawable.flag_france);
                break;
            case "French Polynesia":
                holder.flagView.setImageResource(R.drawable.flag_french_polynesia);
                break;
            case "Gabon":
                holder.flagView.setImageResource(R.drawable.flag_gabon);
                break;
            case "Gambia":
                holder.flagView.setImageResource(R.drawable.flag_gambia);
                break;
            case "Georgia":
                holder.flagView.setImageResource(R.drawable.flag_georgia);
                break;
            case "Germany":
                holder.flagView.setImageResource(R.drawable.flag_germany);
                break;
            case "Ghana":
                holder.flagView.setImageResource(R.drawable.flag_ghana);
                break;
            case "Gibraltar":
                holder.flagView.setImageResource(R.drawable.flag_gibraltar);
                break;
            case "Greece":
                holder.flagView.setImageResource(R.drawable.flag_greece);
                break;
            case "Greenland":
                holder.flagView.setImageResource(R.drawable.flag_greenland);
                break;
            case "Grenada":
                holder.flagView.setImageResource(R.drawable.flag_grenada);
                break;
            case "Guam":
                holder.flagView.setImageResource(R.drawable.flag_guam);
                break;
            case "Guatemala":
                holder.flagView.setImageResource(R.drawable.flag_guatemala);
                break;
            case "Guernsey":
                holder.flagView.setImageResource(R.drawable.flag_guernsey);
                break;
            case "Guinea-Bissau":
                holder.flagView.setImageResource(R.drawable.flag_guinea_bissau);
                break;
            case "Guinea":
                holder.flagView.setImageResource(R.drawable.flag_guinea);
                break;
            case "Guyana":
                holder.flagView.setImageResource(R.drawable.flag_guyana);
                break;
            case "Haiti":
                holder.flagView.setImageResource(R.drawable.flag_haiti);
                break;
            case "Honduras":
                holder.flagView.setImageResource(R.drawable.flag_honduras);
                break;
            case "Hong Kong":
                holder.flagView.setImageResource(R.drawable.flag_hong_kong);
                break;
            case "Hungary":
                holder.flagView.setImageResource(R.drawable.flag_hungary);
                break;
            case "Iceland":
                holder.flagView.setImageResource(R.drawable.flag_iceland);
                break;
            case "India":
                holder.flagView.setImageResource(R.drawable.flag_india);
                break;
            case "Indonesia":
                holder.flagView.setImageResource(R.drawable.flag_indonesia);
                break;
            case "Iran":
                holder.flagView.setImageResource(R.drawable.flag_iran);
                break;
            case "Iraq":
                holder.flagView.setImageResource(R.drawable.flag_iraq);
                break;
            case "Ireland":
                holder.flagView.setImageResource(R.drawable.flag_ireland);
                break;
            case "Isle of Man":
                holder.flagView.setImageResource(R.drawable.flag_isle_of_man);
                break;
            case "Israel":
                holder.flagView.setImageResource(R.drawable.flag_israel);
                break;
            case "Italy":
                holder.flagView.setImageResource(R.drawable.flag_italy);
                break;
            case "Cote d\'Ivoire":
                holder.flagView.setImageResource(R.drawable.flag_ivory_coast);
                break;
            case "Jamaica":
                holder.flagView.setImageResource(R.drawable.flag_jamaica);
                break;
            case "Japan":
                holder.flagView.setImageResource(R.drawable.flag_japan);
                break;
            case "Jersey":
                holder.flagView.setImageResource(R.drawable.flag_jersey);
                break;
            case "Jordan":
                holder.flagView.setImageResource(R.drawable.flag_jordan);
                break;
            case "Kazakhstan":
                holder.flagView.setImageResource(R.drawable.flag_kazakhstan);
                break;
            case "Kenya":
                holder.flagView.setImageResource(R.drawable.flag_kenya);
                break;
            case "Kiribati":
                holder.flagView.setImageResource(R.drawable.flag_kiribati);
                break;
            case "Kosovo":
                holder.flagView.setImageResource(R.drawable.flag_kosovo);
                break;
            case "Kuwait":
                holder.flagView.setImageResource(R.drawable.flag_kuwait);
                break;
            case "Kyrgyzstan":
                holder.flagView.setImageResource(R.drawable.flag_kyrgyzstan);
                break;
            case "Lao People\'s Democratic Republic":
                holder.flagView.setImageResource(R.drawable.flag_laos);
                break;
            case "Latvia":
                holder.flagView.setImageResource(R.drawable.flag_latvia);
                break;
            case "Lebanon":
                holder.flagView.setImageResource(R.drawable.flag_lebanon);
                break;
            case "Lesotho":
                holder.flagView.setImageResource(R.drawable.flag_lesotho);
                break;
            case "Liberia":
                holder.flagView.setImageResource(R.drawable.flag_liberia);
                break;
            case "Libya":
                holder.flagView.setImageResource(R.drawable.flag_libya);
                break;
            case "Liechtenstein":
                holder.flagView.setImageResource(R.drawable.flag_liechtenstein);
                break;
            case "Lithuania":
                holder.flagView.setImageResource(R.drawable.flag_lithuania);
                break;
            case "Luxembourg":
                holder.flagView.setImageResource(R.drawable.flag_luxembourg);
                break;
            case "Macau":
                holder.flagView.setImageResource(R.drawable.flag_macao);
                break;
            case "Madagascar":
                holder.flagView.setImageResource(R.drawable.flag_madagascar);
                break;
            case "Malawi":
                holder.flagView.setImageResource(R.drawable.flag_malawi);
                break;
            case "Malaysia":
                holder.flagView.setImageResource(R.drawable.flag_malaysia);
                break;
            case "Maldives":
                holder.flagView.setImageResource(R.drawable.flag_maldives);
                break;
            case "Mali":
                holder.flagView.setImageResource(R.drawable.flag_mali);
                break;
            case "Malta":
                holder.flagView.setImageResource(R.drawable.flag_malta);
                break;
            case "Marshall Islands":
                holder.flagView.setImageResource(R.drawable.flag_marshall_island);
                break;
            case "Martinique":
                holder.flagView.setImageResource(R.drawable.flag_martinique);
                break;
            case "Mauritania":
                holder.flagView.setImageResource(R.drawable.flag_mauritania);
                break;
            case "Mauritius":
                holder.flagView.setImageResource(R.drawable.flag_mauritius);
                break;
            case "Mexico":
                holder.flagView.setImageResource(R.drawable.flag_mexico);
                break;
            case "Micronesia":
                holder.flagView.setImageResource(R.drawable.flag_micronesia);
                break;
            case "Moldova":
                holder.flagView.setImageResource(R.drawable.flag_moldova);
                break;
            case "Monaco":
                holder.flagView.setImageResource(R.drawable.flag_monaco);
                break;
            case "Mongolia":
                holder.flagView.setImageResource(R.drawable.flag_mongolia);
                break;
            case "Montenegro":
                holder.flagView.setImageResource(R.drawable.flag_montenegro);
                break;
            case "Montserrat":
                holder.flagView.setImageResource(R.drawable.flag_montserrat);
                break;
            case "Morocco":
                holder.flagView.setImageResource(R.drawable.flag_morocco);
                break;
            case "Mozambique":
                holder.flagView.setImageResource(R.drawable.flag_mozambique);
                break;
            case "Myanmar":
                holder.flagView.setImageResource(R.drawable.flag_myanmar);
                break;
            case "Namibia":
                holder.flagView.setImageResource(R.drawable.flag_namibia);
                break;
            case "Nauru":
                holder.flagView.setImageResource(R.drawable.flag_nauru);
                break;
            case "Nepal":
                holder.flagView.setImageResource(R.drawable.flag_nepal);
                break;
            case "The Netherlands":
                holder.flagView.setImageResource(R.drawable.flag_netherlands);
                break;
            case "New Zealand":
                holder.flagView.setImageResource(R.drawable.flag_new_zealand);
                break;
            case "Nicaragua":
                holder.flagView.setImageResource(R.drawable.flag_nicaragua);
                break;
            case "Niger":
                holder.flagView.setImageResource(R.drawable.flag_niger);
                break;
            case "Nigeria":
                holder.flagView.setImageResource(R.drawable.flag_nigeria);
                break;
            case "Niue":
                holder.flagView.setImageResource(R.drawable.flag_niue);
                break;
            case "Norfolk Island":
                holder.flagView.setImageResource(R.drawable.flag_norfolk_island);
                break;
            case "Northern Mariana Islands":
                holder.flagView.setImageResource(R.drawable.flag_northern_marianas_islands);
                break;
            case "North Korea":
                holder.flagView.setImageResource(R.drawable.flag_north_korea);
                break;
            case "Norway":
                holder.flagView.setImageResource(R.drawable.flag_norway);
                break;
            case "Oman":
                holder.flagView.setImageResource(R.drawable.flag_oman);
                break;
            case "Pakistan":
                holder.flagView.setImageResource(R.drawable.flag_pakistan);
                break;
            case "Palau":
                holder.flagView.setImageResource(R.drawable.flag_palau);
                break;
            case "Palestine":
                holder.flagView.setImageResource(R.drawable.flag_palestine);
                break;
            case "Panama":
                holder.flagView.setImageResource(R.drawable.flag_panama);
                break;
            case "Papua New Guinea":
                holder.flagView.setImageResource(R.drawable.flag_papua_new_guinea);
                break;
            case "Paraguay":
                holder.flagView.setImageResource(R.drawable.flag_paraguay);
                break;
            case "Peru":
                holder.flagView.setImageResource(R.drawable.flag_peru);
                break;
            case "Philippines":
                holder.flagView.setImageResource(R.drawable.flag_philippines);
                break;
            case "Pitcairn":
                holder.flagView.setImageResource(R.drawable.flag_pitcairn_islands);
                break;
            case "Portugal":
                holder.flagView.setImageResource(R.drawable.flag_portugal);
                break;
            case "Puerto Rico":
                holder.flagView.setImageResource(R.drawable.flag_puerto_rico);
                break;
            case "Qatar":
                holder.flagView.setImageResource(R.drawable.flag_qatar);
                break;
            case "Macedonia":
                holder.flagView.setImageResource(R.drawable.flag_republic_of_macedonia);
                break;
            case "Poland":
                holder.flagView.setImageResource(R.drawable.flag_republic_of_poland);
                break;
            case "Romania":
                holder.flagView.setImageResource(R.drawable.flag_romania);
                break;
            case "Russia":
                holder.flagView.setImageResource(R.drawable.flag_russia);
                break;
            case "Rwanda":
                holder.flagView.setImageResource(R.drawable.flag_rwanda);
                break;
            case "Reunion":
                holder.flagView.setImageResource(R.drawable.flag_france);
                break;
            case "Saint Kitts and Nevis":
                holder.flagView.setImageResource(R.drawable.flag_saint_kitts_and_nevis);
                break;
            case "Saint-Martin (France)":
                holder.flagView.setImageResource(R.drawable.flag_france);
                break;
            case "El Salvador":
                holder.flagView.setImageResource(R.drawable.flag_salvador);
                break;
            case "Samoa":
                holder.flagView.setImageResource(R.drawable.flag_samoa);
                break;
            case "San Marino":
                holder.flagView.setImageResource(R.drawable.flag_san_marino);
                break;
            case "Sao Tome and Principe":
                holder.flagView.setImageResource(R.drawable.flag_sao_tome_and_principe);
                break;
            case "Saudi Arabia":
                holder.flagView.setImageResource(R.drawable.flag_saudi_arabia);
                break;
            case "Scotland":
                holder.flagView.setImageResource(R.drawable.flag_scotland);
                break;
            case "Senegal":
                holder.flagView.setImageResource(R.drawable.flag_senegal);
                break;
            case "Serbia":
                holder.flagView.setImageResource(R.drawable.flag_serbia);
                break;
            case "Seychelles":
                holder.flagView.setImageResource(R.drawable.flag_seychelles);
                break;
            case "Sierra Leone":
                holder.flagView.setImageResource(R.drawable.flag_sierra_leone);
                break;
            case "Singapore":
                holder.flagView.setImageResource(R.drawable.flag_singapore);
                break;
            case "Sint Maarten (Dutch part)":
                holder.flagView.setImageResource(R.drawable.flag_sint_maarten);
                break;
            case "Slovakia":
                holder.flagView.setImageResource(R.drawable.flag_slovakia);
                break;
            case "Slovenia":
                holder.flagView.setImageResource(R.drawable.flag_slovenia);
                break;
            case "Solomon Islands":
                holder.flagView.setImageResource(R.drawable.flag_solomon_islands);
                break;
            case "Somalia":
                holder.flagView.setImageResource(R.drawable.flag_somalia);
                break;
            case "South Africa":
                holder.flagView.setImageResource(R.drawable.flag_south_africa);
                break;
            case "South Korea":
                holder.flagView.setImageResource(R.drawable.flag_south_korea);
                break;
            case "South Sudan":
                holder.flagView.setImageResource(R.drawable.flag_south_sudan);
                break;
            case "Spain":
                holder.flagView.setImageResource(R.drawable.flag_spain);
                break;
            case "Sri Lanka":
                holder.flagView.setImageResource(R.drawable.flag_sri_lanka);
                break;
            case "Saint Bartholemy":
                holder.flagView.setImageResource(R.drawable.flag_st_barts);
                break;
            case "Saint Lucia":
                holder.flagView.setImageResource(R.drawable.flag_st_lucia);
                break;
            case "Saint Vincent and the Grenadines":
                holder.flagView.setImageResource(R.drawable.flag_st_vincent_and_the_grenadines);
                break;
            case "Sudan":
                holder.flagView.setImageResource(R.drawable.flag_sudan);
                break;
            case "Suriname":
                holder.flagView.setImageResource(R.drawable.flag_suriname);
                break;
            case "Swaziland":
                holder.flagView.setImageResource(R.drawable.flag_swaziland);
                break;
            case "Sweden":
                holder.flagView.setImageResource(R.drawable.flag_sweden);
                break;
            case "Switzerland":
                holder.flagView.setImageResource(R.drawable.flag_switzerland);
                break;
            case "Syria":
                holder.flagView.setImageResource(R.drawable.flag_syria);
                break;
            case "Taiwan":
                holder.flagView.setImageResource(R.drawable.flag_taiwan);
                break;
            case "Tajikistan":
                holder.flagView.setImageResource(R.drawable.flag_tajikistan);
                break;
            case "Tanzania":
                holder.flagView.setImageResource(R.drawable.flag_tanzania);
                break;
            case "Thailand":
                holder.flagView.setImageResource(R.drawable.flag_thailand);
                break;
            case "Togo":
                holder.flagView.setImageResource(R.drawable.flag_togo);
                break;
            case "Tokelau":
                holder.flagView.setImageResource(R.drawable.flag_tokelau);
                break;
            case "Tonga":
                holder.flagView.setImageResource(R.drawable.flag_tonga);
                break;
            case "Trinidad and Tobago":
                holder.flagView.setImageResource(R.drawable.flag_trinidad_and_tobago);
                break;
            case "Tunisia":
                holder.flagView.setImageResource(R.drawable.flag_tunisia);
                break;
            case "Turkey":
                holder.flagView.setImageResource(R.drawable.flag_turkey);
                break;
            case "Turkmenistan":
                holder.flagView.setImageResource(R.drawable.flag_turkmenistan);
                break;
            case "Turks and Caicos Islands":
                holder.flagView.setImageResource(R.drawable.flag_turks_and_caicos);
                break;
            case "Tuvalu":
                holder.flagView.setImageResource(R.drawable.flag_tuvalu);
                break;
            case "Uganda":
                holder.flagView.setImageResource(R.drawable.flag_uganda);
                break;
            case "Ukraine":
                holder.flagView.setImageResource(R.drawable.flag_ukraine);
                break;
            case "United Arab Emirates":
                holder.flagView.setImageResource(R.drawable.flag_united_arab_emirates);
                break;
            case "United Kingdom":
                holder.flagView.setImageResource(R.drawable.flag_united_kingdom);
                break;
            case "United States of America":
                holder.flagView.setImageResource(R.drawable.flag_united_states_of_america);
                break;
            case "Uruguay":
                holder.flagView.setImageResource(R.drawable.flag_uruguay);
                break;
            case "Uzbekistan":
                holder.flagView.setImageResource(R.drawable.flag_uzbekistn);
                break;
            case "Vanuatu":
                holder.flagView.setImageResource(R.drawable.flag_vanuatu);
                break;
            case "Vatican":
                holder.flagView.setImageResource(R.drawable.flag_vatican_city);
                break;
            case "Venezuela":
                holder.flagView.setImageResource(R.drawable.flag_venezuela);
                break;
            case "Vietnam":
                holder.flagView.setImageResource(R.drawable.flag_vietnam);
                break;
            case "Virgin Islands(U.S.)":
                holder.flagView.setImageResource(R.drawable.flag_virgin_islands);
                break;
            case "Wales":
                holder.flagView.setImageResource(R.drawable.flag_wales);
                break;
            case "Western Sahara":
                holder.flagView.setImageResource(R.drawable.flag_western_sahara);
                break;
            case "Yemen":
                holder.flagView.setImageResource(R.drawable.flag_yemen);
                break;
            case "Zambia":
                holder.flagView.setImageResource(R.drawable.flag_zambia);
                break;
            case "Zimbabwe":
                holder.flagView.setImageResource(R.drawable.flag_zimbabwe);
                break;
            case "Northern Ireland":
                holder.flagView.setImageResource(R.drawable.flag_united_kingdom);
                break;
            default:
                holder.flagView.setImageResource(R.drawable.flag_united_nations);
                break;

        }

    }
}





