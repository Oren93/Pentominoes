// Hvert tilvik af Pento stendur fyrir fimmferning (pentomino),
// �.e. fimm samlo�andi ferninga, til d�mis
//
//               #         #                                  #
//    ##### e�a ### e�a #### e�a #### en ekki, til d�mis, ####
//               #               #
//
// Aftasta d�mi� er ekki l�glegur fimmferningur �v� hann er ekki
// samlo�andi � hli�um ferninganna.
// Fyrir hvern sl�kan fimmferning h�fum vi� fimm hnit (x,y) fyrir
// ferningana sem fimmferningurinn samanstendur af. Vi� r��um �essum
// fimm hnitum � nokkurs konar stafr�fsr�� og heimtum a� fyrsta
// hniti� s� (0,0).  Stafr�fsr��in felst � �v� a� 
//   (x1,y1) < (x2,y2) <==> y1 < y2 e�a (y1 = y2 og x1 < x2).
// Skilyr�i� a� fyrsta hniti� s� (0,0) kemur ekki � veg fyrir a�
// vi� getum t�kna� hva�a fimmferning sem er, heldur veldur �v�
// a� hver fimmferningur (e�a sn�ningur � fimmferningi e�a speglun
// � fimmferningi) hefur eina og a�eins eina t�knun sem fimm
// mismunandi hnit � vaxandi r�� me� fyrsta hniti� sem (0,0).
// Fyrir umfj�llun um fimmferninga (pentomino) m� k�kja � vefs��urnar
//   https://web.ma.utexas.edu/users/smmg/archive/1997/radin.html
// og
//   https://en.wikipedia.org/wiki/Pentomino

// H�fundur: Snorri Agnarsson, 2020

import java.util.*;
import java.awt.*;
import java.awt.print.*;

// Tilvik af Pento standa fyrir fimmferning � einhverju
// sn�nings�standi e�a speglunar�standi.
public class Pento
{
    private int[] x = new int[4];
    private int[] y = new int[4];
    private final char n;
    // Fastayr�ing gagna.
    //  Ferningarnir � fimmferningnum hafa hnitin
    //      (0,0),(x[0],y[0]),...,(x[3],y[3])
    //  og tryggt er a� y[0],...,y[3] eru >=0.
    //  Stafbreytan n inniheldur �ann staf sem er
    //  r�tt nafn fimmferningsins, einn af st�funum
    //  F,I,L,P,N,T,U,V,W,X,Y,Z.
    //  Hnitin
    //      (0,0),(x[0],y[0]),...,(x[3],y[3])
    //  eru � vaxandi r�� mi�a� vi� r��ina sem
    //  skilgreind er a� ofan.
    
    // Notkun: Pento p = new Pento(n,x,y);
    // Fyrir:  x og y skilgreina l�glegan fimmferning og
    //         n er stafurinn sem er nafn �ess fimmfernings.
    // Eftir:  p v�sar � tilvik af Pento sem stendur fyrir
    //         vi�komandi fimmferning.
    private Pento( char n, int[] x, int[] y )
    {
        this.x = x;
        this.y = y;
        this.n = n;
    }
    
    // Notkun: Pento p = new Pento(n,s);
    // Fyrir:  Sama og fyrir new Pento(n,s,"","")
    // Eftir:  Sama og fyrir new Pento(n,s,"","")
    // Ath.:   n hl�tur a� vera 'I' og s hl�tur
    //         a� vera "#####", e�a svipa�, svo
    //         sem " ##### ".
    public Pento( char n, String s )
    {
        this(n,s,"","");
    }
    
    // Notkun: Pento p = new Pento(n,s1,s2);
    // Fyrir:  Sama og fyrir new Pento(n,s1,s2,"")
    // Eftir:  Sama og fyrir new Pento(n,s1,s2,"")
    public Pento( char n, String s1, String s2 )
    {
        this(n,s1,s2,"");
    }
    
    // Notkun: Pento p = new Pento(n,s1,s2,s3);
    // Fyrir:  n er l�glegt nafn � fimmferningi og
    //         strengirnir s1,s2,s3 innihalda stafi '#'
    //         �ar sem ferningar � fimmferningnum hafa
    //         hnit og innihalda stafi ' ' annars sta�ar.
    //         Ef strengirnir s1,s2,s3 eru skrifa�ir,
    //         l�nu eftir l�nu, �� s�st l�gun
    //         fimmferningsins.
    // Eftir:  p v�sar � tilvik af Pento sem samsvarar
    //         �eirri l�gun sem s1,s2,s3 l�sa.
    public Pento( char n, String s1, String s2, String s3 )
    {
        this.n = n;
        int count = 0;
        int firstX = 0;
        for( int i=0 ; i!=s1.length() ; i++ )
        {
            if( s1.charAt(i) == ' ' ) continue;
            if( s1.charAt(i) != '#' ) throw new Error();
            if( count == 5 ) throw new Error();
            if( count == 0 )
            {
                firstX = i;
            }
            else
            {
                x[count-1] = i-firstX;
                y[count-1] = 0;
            }
            count++;
        }
        if( count == 0 ) throw new Error();
        for( int i=0 ; i!=s2.length() ; i++ )
        {
            if( s2.charAt(i) == ' ' ) continue;
            if( s2.charAt(i) != '#' ) throw new Error();
            if( count == 5 ) throw new Error();
            x[count-1] = i-firstX;
            y[count-1] = 1;
            count++;
        }
        for( int i=0 ; i!=s3.length() ; i++ )
        {
            if( s3.charAt(i) == ' ' ) continue;
            if( s3.charAt(i) != '#' ) throw new Error();
            if( count == 5 ) throw new Error();
            x[count-1] = i-firstX;
            y[count-1] = 2;
            count++;
        }
    }

    // Notkun: int x = p.getX(i);
    // Fyrir:  p v�sar � l�glegt tilvik af Pento,
    //         0 <= i <=4
    // Eftir:  x inniheldur x-hnit fernings i innan p.
    public int getX( int i )
    {
        if( i==0 ) return 0;
        return x[i-1];
    }
    
    // Notkun: int y = p.getY(i);
    // Fyrir:  p v�sar � l�glegt tilvik af Pento,
    //         0 <= i <=4
    // Eftir:  y inniheldur y-hnit fernings i innan p.
    public int getY( int i )
    {
        if( i==0 ) return 0;
        return y[i-1];
    }

    // Notkun: boolean eq = p1.equals(p2);
    // Fyrir:  p1 og p2 v�sa � l�gleg tilvik af Pento.
    // Eftir:  eq inniheldur true �� og �v� a�eins a�
    //         p1 og p2 s�u eins, �.e. s�u sami
    //         fimmferningur � sama sn�nings�standi og
    //         speglunar�standi, �.e. a� hnit allra
    //         ferninganna � p1 og p2 s�u s�mu.
    public boolean equals( Object f )
    {
        if( f == this ) return true;
        if( f.getClass() != this.getClass() ) return false;
        Pento fp = (Pento)f;
        for( int i=1 ; i!=5 ; i++ )
        {
            // B�i� er a� sta�festa a� this.getX(j)==f.getX(j)
            // og this.getY(j)==f.getY(j) fyrir j=0,...,i-1
            if( getX(i) != fp.getX(i) ) return false;
            if( getY(i) != fp.getY(i) ) return false;
        }
        return true;
    }

    // Notkun: int h = p.hashCode();
    // Fyrir:  p v�sar � tilvik af Pento.
    // Eftir:  h er t�tigildi sem tryggt er a� uppfylli
    //         samninginn fyrir hashCode(), sem segir
    //         a� ef
    //            p1.equals(p2)
    //         �� er
    //            p1.hashCode() == p2.hashCode()
    public int hashCode()
    {
        int res = 0;
        int[] mx = new int[]{3,5,7,11};
        int[] my = new int[]{13,17,19,23};
        for( int i=0 ; i!=4 ; i++ )
        {
            res += x[i]*mx[i];
            res += y[i]*my[i];
        }
        return res;
    }
    
    // Notkun: boolean l = less(a,b,i,j);
    // Fyrir:  a[i],a[j],b[i],b[j] eru til.
    // Eftir:  l er satt �� og �v� a�eins a�
    //           (a[i],b[i]) < (a[j],b[j])
    //         mi�a� vi� r��ina sem skilgreind
    //         er fyrir hnit a� ofan.
    private static boolean less( int[] a, int[] b, int i, int j )
    {
        if( b[i] < b[j] ) return true;
        if( b[i] > b[j] ) return false;
        return a[i] < a[j];
    }
    
    // Notkun: boolean l = less(a,b,i,j);
    // Fyrir:  a[i],a[j],b[i],b[j] eru til.
    // Eftir:  B�i� er a� v�xla a[i] me� a[j]
    //         og b[i] me� b[j].
    private static void swap( int[] a, int[] b, int i, int j )
    {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
        t = b[i];
        b[i] = b[j];
        b[j] = t;
    }

    // Notkun: sort5(a,b);
    // Fyrir:  a og b eru int[] me� 5 s�tum.
    // Eftir:  B�i� er a� endurra�a a og b � takt
    //         �annig a� hnitasafni�
    //           {(a[0],b[0]),...,(a[4],b[4])}
    //         er �breytt, en er n� � vaxandi r��.
    private static void sort5( int[] a, int[] b )
    {
        if( less(a,b,1,0) ) swap(a,b,1,0);
        if( less(a,b,2,0) ) swap(a,b,2,0);
        if( less(a,b,3,0) ) swap(a,b,3,0);
        if( less(a,b,4,0) ) swap(a,b,4,0);
        if( less(a,b,2,1) ) swap(a,b,2,1);
        if( less(a,b,3,1) ) swap(a,b,3,1);
        if( less(a,b,4,1) ) swap(a,b,4,1);
        if( less(a,b,3,2) ) swap(a,b,3,2);
        if( less(a,b,4,2) ) swap(a,b,4,2);
        if( less(a,b,4,3) ) swap(a,b,4,3);
    }
    
    // Notkun: normalize(a,b);
    // Fyrir:  a og b eru int[5] sem innihalda hnit
    //          (a[0],b[0]),...,(a[4],b[4])
    //         sem skilgreina l�glegan fimmferning
    //         einhvers sta�ar � hnitakerfinu.
    // Eftir:  B�i� er a� f�ra fimmferninginn til �
    //         hnitakerfinu, �samt �v� a� endurra�a
    //         punktunum �annig a� l�gunin er �breytt,
    //         en (a[0],b[0]) == (0,0) og punktarnir
    //          (a[0],b[0]),...,(a[4],b[4])
    //         eru n� � vaxandi r��.
    private static void normalize( int[] a, int[] b )
    {
        sort5(a,b);
        int minb = b[0], mina = a[0];
        for( int i=1 ; i!=5 ; i++ )
        {
            if( b[i] < minb )
            {
                mina = a[i];
                minb = b[i];
            }
        }
        for( int i=0 ; i!=5 ; i++ )
        {
            a[i] = a[i]-mina;
            b[i] = b[i]-minb;
        }
    }

    // Notkun: Pento p2 = flip(p1);
    // Fyrir:  p1 er tilv�sun � l�glegan fimmferning.
    // Eftir:  p2 er tilv�sun � n�jan l�glegan fimmferning
    //         sem er �tkoman �egar p1 er spegla�ur um y-�sinn.
    static Pento flip( Pento f )
    {
        int[] a = new int[5];
        int[] b = new int[5];
        for( int i=0 ; i!=5 ; i++ )
        {
            a[i] = -f.getX(i);
            b[i] = f.getY(i);
        }
        sort5(a,b);
        normalize(a,b);
        int[] ap = new int[4];
        int[] bp = new int[4];
        for( int i=0 ; i!=4 ; i++ )
        {
            ap[i] = a[i+1];
            bp[i] = b[i+1];
        }
        return new Pento(f.n,ap,bp);
    }
    
    // Notkun: Pento p2 = flip(p1);
    // Fyrir:  p1 er tilv�sun � l�glegan fimmferning.
    // Eftir:  p2 er tilv�sun � n�jan l�glegan fimmferning
    //         sem er �tkoman �egar p1 er sn�i� 90 gr��ur.
    //         Sn�ningurinn er r�tts�lis s� mi�a� vi�
    //         hnitakerfi �ar sem x-�sinn �visar til h�gri
    //         og y-�sinn v�sar ni�ur.
    static Pento rotate( Pento f )
    {
        int[] a = new int[5];
        int[] b = new int[5];
        for( int i=0 ; i!=5 ; i++ )
        {
            a[i] = -f.getY(i);
            b[i] = f.getX(i);
        }
        sort5(a,b);
        normalize(a,b);
        int[] ap = new int[4];
        int[] bp = new int[4];
        for( int i=0 ; i!=4 ; i++ )
        {
            ap[i] = a[i+1];
            bp[i] = b[i+1];
        }
        return new Pento(f.n,ap,bp);
    }

    // Notkun: Pento[] v = variants(f);
    // Fyrir:  f v�sar � l�glegan Pento hlut.
    // Eftir:  v v�sar � fylki sem inniheldur l�glega
    //         Pento hluti sem eru allir sn�ningar og
    //         allar speglanir � f, �ar me� tali� f
    //         sj�lfur.  Engir sn�ningar e�a speglanir
    //         eru endurteknar.
    public static Pento[] variants( Pento f )
    {
        HashSet<Pento> s = new HashSet<Pento>();
        for( int i=0 ; i!=4 ; i++ )
        {
            s.add(f);
            f = rotate(f);
        }
        f = flip(f);
        for( int i=0 ; i!=4 ; i++ )
        {
            s.add(f);
            f = rotate(f);
        }
        Pento[] res = new Pento[s.size()];
        int count = 0;
        for( Pento fp: s )
        {
            res[count++] = fp;
        }
        return res;
    }
    
    // Notkun: Pento[][] a = Pento.generateAll();
    // Fyrir:  Ekkert.
    // Eftir:  a v�sar � n�tt 12 staka fylki af fylkjum af Pento hlutum, sem allir
    //         eru mismunandi. Hins vegar inniheldur a[i], fyrir i=0,...,11 fylki
    //         af Pento hlutum sem eru anna�hvort sn�ningar e�a speglanir af hvorum
    //         ��rum.  Allir m�gulegir Pento hlutir eru einhvers sta�ar � a. Allir
    //         sn�ningarinir og allar speglanirnar eru mismunandi.
    public static Pento[][] generateAll()
    {
        Pento[][] f = new Pento[12][];
        f[0] = variants(new Pento('F'," ##"
                                     ,"##"
                                     ," #"
                                 )
                       );
        f[1] = variants(new Pento('I',"#####"));
        
        f[2] = variants(new Pento('L',"####"
                                     ,"#"
                                     )
                       );
        f[3] = variants(new Pento('P',"###"
                                     ,"##"
                                 )
                       );
        f[4] = variants(new Pento('N',"  ##"
                                     ,"###"
                                 )
                       );
        f[5] = variants(new Pento('T',"###"
                                     ," #"
                                     ," #"
                                 )
                       );
        f[6] = variants(new Pento('U',"###"
                                     ,"# #"
                                 )
                       );
        f[7] = variants(new Pento('V',"###"
                                     ,"#"
                                     ,"#"
                                 )
                       );
        f[8] = variants(new Pento('W',"##"
                                     ," ##"
                                     ,"  #"
                                 )
                       );
        f[9] = variants(new Pento('X'," #"
                                     ,"###"
                                     ," #"
                                 )
                       );
        f[10] = variants(new Pento('Y',"####"
                                      ," #"
                                  )
                        );
        f[11] = variants(new Pento('Z',"##"
                                      ," #"
                                      ," ##"
                                  )
                        );
        return f;
    }
    
    // Notkun: boolean oob = outOfBounds(board,x,y);
    // Fyrir:  x og y eru heilt�lur, board er char[][] sem inniheldur
    //         engin null undirfylki.
    // Eftir:  oob er satt �� og �v� a�eins a� board[x][y] s�
    //         ekki l�gleg tilv�sun � stafas�ti.
    public static boolean outOfBounds( char[][] board, int x, int y )
    {
        if( x >= board.length ) return true;
        if( x < 0 ) return true;
        if( y >= board[x].length ) return true;
        return false;
    }

    // Notkun: boolean ok = insert(b,x,y,p);
    // Fyrir:  b er char[][] me� engin null undirfylki.
    //         x og y eru heilt�lur.
    //         p er tilv�sun � l�glegan Pento hlut.
    // Eftir:  ok er satt �� og �v� a�eins a� s�tin
    //         � b sem p tekur, ef fyrsti punktur hans
    //         er settur � (x,y) hafi �ll innihaldi� ' '
    //         �egar kalla� var.  �� er einnig b�i� a� setja
    //         stafinn sem einkennir p � �au s�ti.  A� ��rum
    //         kosti er ok �satt og b er �breytt.
    public static boolean insert( char[][] b, int x, int y, Pento p )
    {
        if( outOfBounds(b,p.getX(1)+x,p.getY(1)+y) ) return false;
        if( outOfBounds(b,p.getX(2)+x,p.getY(2)+y) ) return false;
        if( outOfBounds(b,p.getX(3)+x,p.getY(3)+y) ) return false;
        if( outOfBounds(b,p.getX(4)+x,p.getY(4)+y) ) return false;
        if( b[p.getX(1)+x][p.getY(1)+y] != ' ' ) return false;
        if( b[p.getX(2)+x][p.getY(2)+y] != ' ' ) return false;
        if( b[p.getX(3)+x][p.getY(3)+y] != ' ' ) return false;
        if( b[p.getX(4)+x][p.getY(4)+y] != ' ' ) return false;
        b[x][y] = p.n;
        b[p.getX(1)+x][p.getY(1)+y] = p.n;
        b[p.getX(2)+x][p.getY(2)+y] = p.n;
        b[p.getX(3)+x][p.getY(3)+y] = p.n;
        b[p.getX(4)+x][p.getY(4)+y] = p.n;
        return true;
    }
    
    // Notkun: remove(b,x,y,p);
    // Fyrir:  b er bor� sem inniheldur p me� minnstu hnit � (x,y).
    // Eftir:  B�i� er a� fjarl�gja p af b og setja ' ' � hnitin sem
    //         ��ur innih�ldu stafinn sem einkennir p.
    public static void remove( char[][] b, int x, int y, Pento p )
    {
        b[x][y] = ' ';
        b[p.getX(1)+x][p.getY(1)+y] = ' ';
        b[p.getX(2)+x][p.getY(2)+y] = ' ';
        b[p.getX(3)+x][p.getY(3)+y] = ' ';
        b[p.getX(4)+x][p.getY(4)+y] = ' ';
    }

    // Notkun: generateSolutions(a,board,used,iter,partial);
    // Fyrir:  a er Pento[][] sem inniheldur alla fimmferninga,
    //         eins og �tkoman �r generateAll().
    //         board er char[][].
    //         used er boolean[] af st�r� 12.
    //         iter v�sar � flakkara af tagi MyIterator.
    //         partial er boolean gildi.
    // Eftir:  B�i� er a� senda allar lausnir inn � iter me�
    //         put() bo�inu � iter, �ar sem lausnirnar uppfylla
    //         �a� skilyr�i a� b�i� er a� fylla �t � �ll s�ti �
    //         board sem innihalda ' ' me� �v� a� setja � bor�i�
    //         � mesta lagi eitt af hverjum �eim Pento hlutum (�
    //         einhverjum sn�ningi e�a speglun) sem ekki hafa true
    //         � �v� s�ti � used sem samsvarar hlutnum, �annig a�
    //         hlutirnir �ekji n�kv�mlega alla �� reiti sem
    //         innih�ldur ' '.
    //         Ef partial er satt (true) �� eru ekki einungis
    //         fullar lausnir sendar � flakkarann iter heldur
    //         einnig �au bor� sem ver�a til �egar leita� er,
    //         �ar sem ekki er b�i� a� fylla � alla au�a reiti.
    //         Bor�i� board og fylki� used eru �breytt fr� �v�
    //         fyrir kalli�.
    public static void generateSolutions( Pento[][] a, char[][] board, boolean[] used, MyIterator iter, boolean partial )
        throws InterruptedException
    {
        int x=0,y=0;
        boolean found=false;
        for( int i=0 ; i!=board[0].length && !found ; i++ )
        {
            for( int j=0 ; j!=board.length && !found ; j++ )
            {
                if( board[j][i] == ' ' )
                {
                    found=true;
                    x = j;
                    y = i;
                }
            }
        }
        if( !found )
        {
            if( !partial ) iter.put(copy(board));
            return;
        }
        for( int i=0 ; i!=used.length ; i++ )
        {
            if( used[i] ) continue;
            for( int j=0 ; j!=a[i].length ; j++ )
            {
                if( insert(board,x,y,a[i][j]) )
                {
                    if( partial ) iter.put(copy(board));
                    used[i] = true;
                    generateSolutions(a,board,used,iter,partial);
                    remove(board,x,y,a[i][j]);
                    used[i] = false;
                }
            }
        }
    }
    
    // Notkun: String[] b = copy(board);
    // Fyrir:  board er char[][] sem samsvarar �standi � �rautabor�i.
    // Eftir:  b er String[] sem samsvarar board.
    private static String[] copy( char[][] b )
    {
        String[] b2 = new String[b.length];
        for( int i=0 ; i!=b.length ; i++ ) b2[i] = new String(b[i]);
        return b2;
    }
    
    // Notkun: char[][] b = makeBoard(board);
    // Fyrir:  board er String[] sem samsvarar fimmferninga�raut.
    // Eftir:  b er char[][] sem samsvarar s�mu �raut.
    public static char[][] makeBoard( String... s )
    {
        char[][] board = new char[s.length][];
        for( int i=0 ; i!=s.length ; i++ )
        {
            board[i] = new char[s[i].length()];
            for( int j=0 ; j!=s[i].length() ; j++ )
                board[i][j] = s[i].charAt(j);
        }
        return board;
    }
    
    // Hlutir af tagi MyIterator eru flakkarar (iterators) sem
    // framlei�a allar lausnir, �n endurtekninga, fyrir �rautina
    // sem felst � a� fylla �t � alla au�a reiti � bor�i me�
    // fimmferningum �n �ess a� nota neinn fimmferning oftar en
    // einu sinni.
    private static class MyIterator implements Iterator<String[]>
    {
        private String[] board = null;
        private boolean hasValue = false;
        private boolean done = false;
        // Fastayr�ing gagna:
        //  B�i� er a� r�sa �r�� sem framlei�ir allar lausnir og
        //  setur ��r � �ennan hlut gegnum put() bo�i�.
        //
        //  B�i� er a� s�kja n�ll e�a fleiri sl�kar lausnir.  S��asta
        //  gildi� sem framlei�slu�r��urinn sendir � put var e�a ver�ur
        //  new String[1], �.e. fylki af tagi String sem inniheldur
        //  null � s�ti 0.
        //  
        //  B�i� er a� flakka gegnum n�ll e�a fleiri af lausnunum
        //  sem framleiddar eru. Ef n�sta lausn � eftir er komin fr�
        //  framlei�slu�r��inum �� er h�n � tilviksbreytunni board
        //  og �� er hasValue satt.  Annars er hasValue �satt.
        //  Ef b�i� er a� flakka yfir allar lausnir �� er done satt,
        //  annars er done �satt.
        
        // Notkun: MyIterator it = new MyIterator(board,partial);
        // Fyrir:  board er String[] sem samsvarar fimmferningar�raut.
        // Eftir:  it v�sar � n�jan flakkara sem skilar �llum lausnum
        //         � �rautinni og ef partial er satt einnig �eim ��rum
        //         bor�um sem ver�a til � leit a� lausn.
        private MyIterator( String[] board, boolean partial )
        {
            Runnable r =
                ()->
                {
                    try
                    {
                        Pento[][] a = generateAll();
                        boolean[] used = new boolean[12];
                        for( int i=0 ; i!=12 ; i++ ) used[i] = false;
                        generateSolutions(a,makeBoard(board),used,MyIterator.this,partial);
                        MyIterator.this.put(new String[1]);
                    }
                    catch( InterruptedException e )
                    {
                        e.printStackTrace();
                    }
                };
            new Thread(r).start();
        }

        // Notkun: it.put(board);
        // Fyrir:  it er flakkari sem flakka� hefur yfir n�ll
        //         e�a fleiri bor� innan runu sinna lausna.
        //         board er n�sta lausn sem it �tti a� skila.
        // Eftir:  Flakkarinn hefur teki� vi� board og mun
        //         koma �eirri lausn til skila ef be�i� er um.
        private synchronized void put( String[] board )
            throws InterruptedException
        {
            while( hasValue ) this.wait();
            this.board = board;
            if( board[0] == null )
                done = true;
            else
                hasValue = true;
            this.notifyAll();
        }

        // Notkun: boolean hn = it.hasNext();
        // Fyrir:  it v�sar � MyIterator.
        // Eftir:  hn er satt �� og �v� a�eins a� flakkarinn
        //         geti skila� a.m.k. einni enn lausn me�
        //         kalli � next().
        public synchronized boolean hasNext()
        {
            try
            {
                while( !hasValue && !done ) wait();
                return !done;
            }
            catch( InterruptedException e )
            {
                return false;
            }
        }

        // Notkun: String[] b = it.next();
        // Fyrir:  B�i� er a� sta�festa me� kalli � it.hasNext()
        //         a� it geti skila� a.m.k. einni enn lausn.
        // Eftir:  b v�sar � n�ja lausn sem er n�sta lausn �
        //         runu �eirra lausna sem it skilar.
        public synchronized String[] next()
        {
            if( done || !hasValue ) throw new Error();
            String[] res = board;
            hasValue = false;
            notifyAll();
            return res;
        }
    }
    
    // Tilvik af MyIterable eru flakkanleg s�fn af lausnum �
    // tiltekinni fimmferninga�raut.
    public static class MyIterable implements Iterable<String[]>
    {
        private final String[] board;
        private final boolean partial;
        // Fastayr�ing gagna.
        //   board inniheldur �rautina sem veri� er a� leysa.

        // Notkun: MyIterable i = new MyIterable(board);
        // Fyrir:  board er fimmferninga�raut.
        // Eftir:  i er flakkanlegt safn allra lausna � board.
        public MyIterable( String[] board, boolean partial )
        {
            this.board = board;
            this.partial = partial;
        }

        // Notkun: Iterator<String[]> it = i.iterator();
        // Fyrir:  i er tilvik af MyIterable.
        // Eftir:  it er flakkari sem skilar �llum lausnum
        //         � fimmferninga�rautinni sem i leysir.
        public Iterator<String[]> iterator()
        {
            return new MyIterator(board,partial);
        }
    }
    
    // Notkun: Iterable<String[]> iterable = makeSolutions(board);
    // Fyrir:  board er strengjafylki og ekkert s�ti inniheldur null.
    // Eftir:  iterable er flakkanlegt safn allra lausna � �rautinni
    //         sem felst � a� fylla �t � �ll au� s�ti (s�ti me�
    //         bilstaf) � tv�v��a fylkinu af st�fum sem board
    //         samsvarar, me� fimmferningum � �ann h�tt a� enginn
    //         fimmferningur er nota�ur oftar en einu sinni.
    // Ath.:   D�miger� notkun er � for-lykkju, svona:
    //            for( String[] b: makeSolutions(board) )
    //            {
    //                ...me�h�ndla lausnina b...
    //            }
    //         Til d�mis m� telja allar lausnir svona:
    //            int n=0;
    //            for( String[] b: makeSolutions(board) )
    //            {
    //                n++;
    //            }
    //            // n inniheldur n� fj�lda lausna fyrir board
    //         Einnig m� prenta allar lausnir svona:
    //            for( String[] b: makeSolutions(board) )
    //            {
    //                for( String s: b ) System.out.println(s);
    //                System.out.println();
    //            }
    public static MyIterable makeSolutions( String... board )
    {
        return new MyIterable(board,false);
    }
    
    // Notkun: Iterable<String[]> iterable = makePartialSolutions(board);
    // Fyrir:  board er strengjafylki og ekkert s�ti inniheldur null.
    // Eftir:  iterable er flakkanlegt safn allra lausna og hlutlausna
    //         � �rautinni sem felst � a� fylla �t � �ll au� s�ti (s�ti
    //         me� bilstaf) � tv�v��a fylkinu af st�fum sem board
    //         samsvarar, me� fimmferningum � �ann h�tt a� enginn
    //         fimmferningur er nota�ur oftar en einu sinni.
    public static MyIterable makePartialSolutions( String... board )
    {
        return new MyIterable(board,true);
    }
    
    public static void main( String[] args )
        throws InterruptedException
    {
        String[] board;
        /*
        board = new String[]{ "*         *"
                            , "   * * *   "
                            , " *       * "
                            , "   * * *   "
                            , " *       * "
                            , "   * * *   "
                            , "*         *"
                            };
        */
        /*board = new String[]{ "           "
                            , "           "
                            , "           "
                            , "   ******* "
                            , "   ******* "
                            , "   ******* "
                            , "   ******* "
                            , "           "
                            };
        */
        board = new String[]{ "*  ***    *"
                            , "  ***      "
                            , "  ***      "
                            , "   ***     "
                            , "     ***   "
                            , "      ***  "
                            , "      ***  "
                            , "*    ***  *"
                            };
        
        /*
        board = new String[]{ "*  ***    *"
                            , "  ***      "
                            , "  ***      "
                            , "   ***     "
                            , "     ***   "
                            , "      ***  "
                            , "      ***  "
                            , "*    ***  *"
                            };
        */
        /*
        board = new String[]{ "***   ***"
                            , "***   ***"
                            , "***   ***"
                            , "         "
                            , "         "
                            , "         "
                            , "***   ***"
                            , "***   ***"
                            , "***   ***"
                            };
        */
        //board = new String[]{"                    ","                    ","                    "};
        //board = new String[]{"**                    ","*                    *","                    **"};
        //board = new String[]{"   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   "};
        for( String[] b: makeSolutions(board) )
        {
            for( String s: b ) System.out.println(s);
            System.out.println();
        }
        int n = 0;
        board = new String[]{ "        "
                            , "        "
                            , "        "
                            , "   **   "
                            , "   **   "
                            , "        "
                            , "        "
                            , "        "
                            };
        for( String[] b: makeSolutions(board) )
        {
            n++;
            if( n == 1 ) for( String s: b ) System.out.println(s);
        }
        System.out.println(n);

        /*
        board = new String[]{"          ","          ","          ","          ","          ","          "};
        n = 0;
        for( String[] b: makePartialSolutions(board) )
        {
            for( int i=0 ; i!=b[0].length() ; i++ )
            {
                for( int j=0 ; j!=b.length ; j++ )
                    System.out.print(b[j].charAt(i));
                System.out.println();
            }
            System.out.println();
            n++;
        }
        System.out.println(n);
        */
    }
}