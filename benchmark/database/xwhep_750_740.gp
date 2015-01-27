
set title "XWHEP 7.5.0 Vs 7.4.0\nCompleted jobs per minute\n200 TCP handlers"
set xlabel "Minutes"
set ylabel "Jobs"
set encoding iso_8859_1
set key outside
#set key 80, 535
#set key 1,3
#set size 1.3,1
set key spacing 1.5
set key width 1.5
#set logscale y
#set xdata time
#set timefmt "%Y/%m/%d %H:%M"
#set format x "%H:%M"
set xtics rotate



set terminal png
set output 'xwhep_750_740_completeds_per_minute.png'

#plot 'FILEIN_completeds_per_minute.csv' using 1:3 with lines  lt 2  lw 2 title 'Completeds'

set output 'xwhep_750_740_completeds_per_minute.png'

plot 'csv/xwhep750_TCP200_DB60_1457W_completeds_per_minute.csv'  using 3 lt 2 lw 2 title '7.5.0 DB60' with lines,\
     'csv/xwhep750_TCP200_DB120_1466W_completeds_per_minute.csv' using 3 lt 1 lw 2 title '7.5.0 DB120' with lines,\
     'csv/xwhep740_TCP200_DB60_2323W_completeds_per_minute.csv'  using 3 lt 3 lw 2 title '7.4.0 DB60' with lines

