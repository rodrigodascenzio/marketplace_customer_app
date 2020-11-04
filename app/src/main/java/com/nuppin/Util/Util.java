package com.nuppin.Util;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nuppin.model.Address;
import com.nuppin.model.Scheduling;
import com.nuppin.model.CartCompany;
import com.nuppin.model.Company;
import com.nuppin.model.Order;
import com.nuppin.model.Product;
import com.nuppin.model.User;
import com.nuppin.connection.ConnectApi;
import com.nuppin.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by Rodrigo on 9/9/2017.
 */

public class Util {

    public static String formater(double d) {
        DecimalFormat formatador;
        if (d > 1) {
            formatador = new DecimalFormat("0.0 km");
        } else {
            return "~1,0 km";
        }
        return formatador.format(d);
    }

    public static String formaterAll(double d) {
        DecimalFormat formatador;
        formatador = new DecimalFormat("0.0 km");
        return formatador.format(d);
    }

    public static String formaterRating(double d) {
        DecimalFormat formatador;
        formatador = new DecimalFormat("0.00");
        return formatador.format(d);
    }

    public static String formaterPrice(double d) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return numberFormat.format(d);
    }

    public static void hasPhoto(Object o, SimpleDraweeView foto) {
        if (o != null) {
            if (o instanceof Company) {
                if (foto.getId() == R.id.banner) {
                    if (((Company) o).getBanner_photo() != null) {
                        foto.setImageURI(Uri.parse(((Company) o).getBanner_photo()));
                    }
                } else {
                    if (((Company) o).getphoto() != null) {
                        foto.setImageURI(Uri.parse(((Company) o).getphoto()));
                    } else {
                        foto.setImageURI("");
                    }
                }
            } else if (o instanceof CartCompany) {
                if (((CartCompany) o).getphoto() != null) {
                    foto.setImageURI(Uri.parse(((CartCompany) o).getphoto()));
                }
            } else if (o instanceof Product) {
                if (((Product) o).getphoto() != null) {
                    foto.setImageURI(Uri.parse(((Product) o).getphoto()));
                } else {
                    foto.setImageURI("");
                }
            } else if (o instanceof Order) {
                if (((Order) o).getphoto() != null) {
                    foto.setImageURI(Uri.parse(((Order) o).getphoto()));
                } else {
                    foto.setImageURI("");
                }
            } else if (o instanceof User) {
                if (((User) o).getphoto() != null) {
                    foto.setImageURI(Uri.parse(((User) o).getphoto()));
                }
            } else if (o instanceof Scheduling) {
                if (((Scheduling) o).getphoto() != null) {
                    foto.setImageURI(Uri.parse(((Scheduling) o).getphoto()));
                } else {
                    foto.setImageURI("");
                }
            } else if (o instanceof String) {
                if (!o.equals("")) {
                    foto.setImageURI(Uri.parse(o.toString()));
                }
            }
        }
    }

    public static String zeroToCalendar(int day, int month, int year) {
        if (day < 10) {
            if (month < 10) {
                return ("0" + day + "/0" + month + "/" + year);
            } else {
                return ("0" + day + "/" + month + "/" + year);
            }
        } else {
            if (month < 10) {
                return (day + "/0" + month + "/" + year);
            } else {
                return (day + "/" + month + "/" + year);
            }
        }
    }

    public static String timestampFormatDayMonth(String string) {
        String[] separated = string.split("-");
        return separated[2] + "/" + separated[1];
    }

    public static String timestampFormatDayMonthYear(String string) {
        String[] separated = string.split("-");
        return separated[2] + "/" + separated[1] + "/" + separated[0];
    }

    public static String zeroToCalendar(int month, int year) {

        if (month < 10) {
            return ("01" + "-0" + month + "-" + year);
        } else {
            return ("01" + "-" + month + "-" + year);
        }
    }

    public static String zeroToCalendarToMysql(int day, int month, int year) {
        if (day < 10) {
            if (month < 10) {
                return (year + "-0" + month + "-0" + day);
            } else {
                return (year + "-" + month + "-0" + day);
            }
        } else {
            if (month < 10) {
                return (year + "-0" + month + "-" + day);
            } else {
                return (year + "-" + month + "-" + day);
            }
        }
    }

    public static String zeroToCalendarToMysql(int month, int year) {

        if (month < 10) {
            return (year + "-0" + month + "-" + "01");
        } else {
            return (year + "-" + month + "-01");
        }
    }

    public static String zeroToTime(int minute, int hour) {
        if (hour < 10) {
            if (minute < 10) {
                return ("0" + hour + ":0" + minute);
            } else {
                return ("0" + hour + ":" + minute);
            }
        } else {
            if (minute < 10) {
                return (hour + ":0" + minute);
            } else {
                return (hour + ":" + minute);
            }
        }
    }

    public static void setaToolbar(final Fragment fragment, int stringNome, Toolbar toolbar, final Activity actv, boolean changeIconNavigation, int iconNav) {
        final AppCompatActivity activity = (AppCompatActivity) actv;
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            Objects.requireNonNull(activity.getSupportActionBar()).setDisplayShowTitleEnabled(false);
            if (stringNome != 0) {
                toolbar.setTitle(stringNome);
            }
            if (changeIconNavigation) {
                if (iconNav != 0) {
                    activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
                    toolbar.setNavigationIcon(iconNav);
                }
            } else {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity != null) {
                    activity.onBackPressed();
                }
            }
        });
    }

    public static void setaToolbar(final Fragment fragment, String stringNome, Toolbar toolbar, final Activity actv, boolean changeIconNavigation, int iconNav) {
        final AppCompatActivity activity = (AppCompatActivity) actv;
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            Objects.requireNonNull(activity.getSupportActionBar()).setDisplayShowTitleEnabled(false);
            toolbar.setTitle(stringNome);
            if (changeIconNavigation) {
                if (iconNav != 0) {
                    activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
                    toolbar.setNavigationIcon(iconNav);
                }
            } else {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity != null) {
                    activity.onBackPressed();
                }
            }
        });
    }

    public static void setaToolbarActivity(int stringNome, Toolbar toolbar, final Activity actv, boolean changeIconNavigation, int iconNav) {
        final AppCompatActivity activity = (AppCompatActivity) actv;
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            if (stringNome != 0) {
                toolbar.setTitle(stringNome);
            }
            if (changeIconNavigation) {
                activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
                toolbar.setNavigationIcon(iconNav);
            } else {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    public static void backFragmentFunction(Fragment fragment) {
        if (fragment != null && fragment.getFragmentManager() != null) {
            fragment.getFragmentManager().popBackStack();
        }
    }

    public static String joinAddress(String rua, String numero, String bairro, String cidade, String estado, Context context) {
        return context.getResources().getString(R.string.joined_address, rua, numero, bairro, cidade, estado);
    }
    public static String joinAddress(String rua, String numero, String bairro, String cidade, String estado, String complemento, Context context) {
        return context.getResources().getString(R.string.joined_address_complement, rua, numero, bairro, cidade, estado, complemento);
    }

    public static String returnStringLatLonCountr(Context context) {
        return UtilShaPre.getDefaultsString(AppConstants.LATITUDE, context) + "," + UtilShaPre.getDefaultsString(AppConstants.LONGITUDE, context) + "," + UtilShaPre.getDefaultsString(AppConstants.COUNTRY_CODE, context);
    }

    public static Address splitAddressLocation(String string) {
        Address address = new Address();
        String[] separated = string.split(",");
        address.setStreet(separated[0].trim());
        String[] separatedNumBairro = separated[1].split("-");
        address.setStreet_number(separatedNumBairro[0].trim());
        address.setDistrict(separatedNumBairro[1].trim());
        String[] separatedCidadeEstado = separated[2].split("-");
        address.setCity(separatedCidadeEstado[0].trim());
        address.setState_code(separatedCidadeEstado[1].trim());
        return address;
    }

    public static String clearNotNumber(String txt) {
        return txt.replaceAll("[^0-9]", "");
    }

    public static boolean validarTelefone(String telefone) {
        //retira todos os caracteres menos os numeros
        telefone = Util.clearNotNumber(telefone);

        //verifica se tem a qtde de numero correto
        if (!(telefone.length() > 10 && telefone.length() <= 11)) return false;

        //Se tiver 11 caracteres, verificar se começa com 9 o celular
        if (Integer.parseInt(telefone.substring(2, 3)) != 9) return false;

        //DDDs validos
        String[] codigosDDD = {"11", "12", "13", "14", "15", "16", "17", "18", "19",
                "21", "22", "24", "27", "28", "31", "32", "33", "34",
                "35", "37", "38", "41", "42", "43", "44", "45", "46",
                "47", "48", "49", "51", "53", "54", "55", "61", "62",
                "64", "63", "65", "66", "67", "68", "69", "71", "73",
                "74", "75", "77", "79", "81", "82", "83", "84", "85",
                "86", "87", "88", "89", "91", "92", "93", "94", "95",
                "96", "97", "98", "99"};

        //verifica se o DDD é valido
        return Arrays.asList(codigosDDD).indexOf(telefone.substring(0, 2)) != -1;//se passar por todas as validações acima, então está tudo certo
    }

    public static void cancelNotifyOnOff(Context context, int id) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(id);
    }


    public static String nomeDiaSemana(String index) {
        if (index != null) {
            switch (index) {
                case "1":
                    return "Domingo";
                case "2":
                    return "Segunda";
                case "3":
                    return "Terça";
                case "4":
                    return "Quarta";
                case "5":
                    return "Quinta";
                case "6":
                    return "Sexta";
                case "7":
                    return "Sabado";
                default:
                    return "";
            }
        }
        return "";
    }

    public static boolean retryAddressError(Context context) {
        if (UtilShaPre.getDefaultsInt("count_address", context) < 3) {
            UtilShaPre.setDefaults("count_address", UtilShaPre.getDefaultsInt("count_address", context) + 1, context);
            return true;
        }else {
            cleanRetryAddress(context);
            return false;
        }
    }

    public static double roundHalf(double num) {
        return Math.round(num*10)/10.0;
    }

    public static void cleanRetryAddress(Context context) {
        UtilShaPre.setDefaults("count_address", 0, context);
    }

}
