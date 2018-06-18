<?php
    /*
     * Script:    DataTables server-side script for PHP and MySQL
     * Copyright: 2010 - Allan Jardine, 2012 - Chris Wright
     * License:   GPL v2 or BSD (3-point)
     */
     
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Easy set variables
     */
     
     
    /* Indexed column (used for fast and accurate table cardinality) */
    $sIndexColumn = "uid";
     
    /* Database connection information */
    $gaSql['user']       = "root";
    $gaSql['password']   = "";
    $gaSql['dbname']         = "xtremweb";
    $gaSql['server']     = "127.0.0.1";
     
     
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * If you just want to use the basic configuration for DataTables with PHP server-side, there is
     * no need to edit below this line
     */
     
    /*
     * Local functions
     */
    function fatal_error ( $sErrorMessage = '' )
    {
        header( $_SERVER['SERVER_PROTOCOL'] .' 500 Internal Server Error' );
        die( $sErrorMessage );
    }
 
     
    /*
     * MySQL connection
     */
    if ( ! $gaSql['link'] = new mysqli( $gaSql['server'], $gaSql['user'], $gaSql['password'], $gaSql['dbname']  ) )
    {
        fatal_error( 'Could not open connection to server' );
    }
 
    /*
     * Paging
     */
    $sLimit = "";
    if ( isset( $_GET['iDisplayStart'] ) && $_GET['iDisplayLength'] != '-1' )
    {
        $sLimit = "LIMIT ".intval( $_GET['iDisplayStart'] ).", ".
            intval( $_GET['iDisplayLength'] );
    }
     
     
    /*
     * Ordering
     */
    $sOrder = "";
    if ( isset( $_GET['iSortCol_0'] ) )
    {
        $sOrder = "ORDER BY  ";
        for ( $i=0 ; $i<intval( $_GET['iSortingCols'] ) ; $i++ )
        {
            if ( $_GET[ 'bSortable_'.intval($_GET['iSortCol_'.$i]) ] == "true" )
            {
                $sOrder .= $columns[ intval( $_GET['iSortCol_'.$i] ) ]."
                    ".($_GET['sSortDir_'.$i]==='asc' ? 'asc' : 'desc') .", ";
            }
        }
         
        $sOrder = substr_replace( $sOrder, "", -2 );
        if ( $sOrder == "ORDER BY" )
        {
            $sOrder = "";
        }
    }
     
     
    /*
     * Filtering
     * NOTE this does not match the built-in DataTables filtering which does it
     * word by word on any field. It's possible to do here, but concerned about efficiency
     * on very large tables, and MySQL's regex functionality is very limited
     */
    $sWhere = "";
    if ( isset($_GET['sSearch']) && $_GET['sSearch'] != "" )
    {
        $sWhere = "WHERE (";
        for ( $i=0 ; $i<count($columns) ; $i++ )
        {
            if ( isset($_GET['bSearchable_'.$i]) && $_GET['bSearchable_'.$i] == "true" )
            {
                $sWhere .= $columns[$i]." LIKE '%". $gaSql['link']->real_escape_string( $_GET['sSearch'] )."%' OR ";
            }
        }
        $sWhere = substr_replace( $sWhere, "", -3 );
        $sWhere .= ')';
    }
     
    /* Individual column filtering */
    for ( $i=0 ; $i<count($columns) ; $i++ )
    {
        if ( isset($_GET['bSearchable_'.$i]) && $_GET['bSearchable_'.$i] == "true" && $_GET['sSearch_'.$i] != '' )
        {
            if ( $sWhere == "" )
            {
                $sWhere = "WHERE ";
            }
            else
            {
                $sWhere .= " AND ";
            }
            $sWhere .= $columns[$i]." LIKE '%". $gaSql['link']->real_escape_string($_GET['sSearch_'.$i])."%' ";
        }
    }
     
     
    /*
     * SQL queries
     * Get data to display
     */
    $sQuery = "
        SELECT SQL_CALC_FOUND_ROWS ".str_replace(" , ", " ", implode(", ", $columns))."
        FROM   $sTable
        $sWhere
        $sOrder
        $sLimit
    ";

    if ( isset( $_GET['DEBUG'] )) {
	    print $sQuery . "<br>";
	}

    $rResult = $gaSql['link']->query( $sQuery ) or fatal_error( 'MySQL Error: ' . $sQuery . ' '  . mysql_errno() );
     
    /* Data set length after filtering */
    $sQuery = "
        SELECT FOUND_ROWS()
    ";

    if ( isset( $_GET['DEBUG'] )) {
	    print $sQuery . "<br>";
	}
    
    $rResultFilterTotal = $gaSql['link']->query( $sQuery ) or fatal_error( 'MySQL Error: ' . mysql_errno() );
    $rResultFilterTotal->data_seek(0);
    $iFilteredTotal = $rResultFilterTotal->fetch_assoc()["FOUND_ROWS()"] ;

    if ( isset( $_GET['DEBUG'] )) {
	    print $iFilteredTotal . "<br>";
	}
     
    /* Total data set length */
    $sQuery = "
        SELECT COUNT(".$sIndexColumn.")
        FROM   $sTable
    ";

    if ( isset( $_GET['DEBUG'] )) {
	    print $sQuery . "<br>";
	}

    $rResultTotal = $gaSql['link']->query( $sQuery ) or fatal_error( '02 MySQL Error: ' . mysql_errno() );
    $rResultFilterTotal->data_seek(0);
    $iTotal = $rResultFilterTotal->fetch_assoc()["FOUND_ROWS()"] ;

    if ( isset( $_GET['DEBUG'] )) {
	    print $iTotal . "<br>";
	}
     
     
    /*
     * Output
     */
    /*
	$output = array(
        "sEcho" => intval($_GET['sEcho']),
        "iTotalRecords" => $iTotal,
        "iTotalDisplayRecords" => $iFilteredTotal,
        "aaData" => array()
    );
	*/
     
    while ( $aRow = $rResult->fetch_assoc() )
    {
        $row = array();
        for ( $i=0 ; $i<count($columns) ; $i++ )
        {
            if ( $columns[$i] == "version" )
            {
                /* Special output formatting for 'version' column */
                $row[] = ($aRow[ $columns[$i] ]=="0") ? '-' : $aRow[ $columns[$i] ];
            }
            else if ( $columns[$i] != ' ' )
            {
                /* General output */
                $row[] = $aRow[ $columns[$i] ];
            }
        }
        $output['aaData'][] = $row;
    }

     if ( isset( $_GET['DEBUG'] )) {
	    print $output . "<br>";
	}

    $gaSql['link']->close();

    echo json_encode( $output );
?>