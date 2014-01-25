---
layout: page
title: "Shell Snippets"
date: 2013-06-01 11:22
comments: true
sharing: true
footer: true
---

This page is to store/share shell commands I use both at home and work to make my life a little easier.

Convert all spaces and hyphens in all files in a folder to underscores:
```
for i in *; do mv "$i" "$(echo $i | tr ' ' '_' | tr '-' '_')"; done
```

Convert all letters to lowercase in all files in a folder: 

```
for in in *; do mv "$i" "$(echo $i | tr A-Z a-z)"; done
```
    
Or make them into executable scripts you can run on files individually:

#### Repalce hyphens/spaces with underscores in a filename
{% include_code shell/rmspaces %}

#### Convert all letters to lowercase in a filename:
{% include_code shell/downcase %}

Just to save some typing.

I often find myself sifting through large datasets, ie: key/value pairs of a HashMap. When using the inspect function in Eclipse, what you get back is a long line of key/value pairs. 

#### Example:
```
[{long_code_1=, address_number=, long_code_2=, view_desc=CU 21 30 (01 08) - Cap On Losses For Certified Acts Of Terrorism, name_and_address=, termination_date=null, reference_number=0, sequence_number=9, decimal_amount_1=, view_name=CU2130_0108, decimal_amount_2=, decimal_amount_3=, premium_1=, view_type=S, short_code_2=, description=, premium_3=, short_code_1=, premium_2=, effective_date=2012-09-11, identifier=1jR, date_2=, date_1=}, {long_code_1=, address_number=, long_code_2=, view_desc=CU 00 01 (12 07) - Commercial Liability Umbrella Coverage Form, name_and_address=, termination_date=null, reference_number=0, sequence_number=1, decimal_amount_1=, view_name=CU_0001_1207, decimal_amount_2=, decimal_amount_3=, premium_1=, view_type=S, short_code_2=, description=, premium_3=, short_code_1=, premium_2=, effective_date=2012-09-11, identifier=1kB, date_2=, date_1=}, {long_code_1=, address_number=, long_code_2=, view_desc=CU 02 03 (12 07) - Florida Changes - Cancellation and Nonrenewal, name_and_address=, termination_date=null, reference_number=0, sequence_number=1, decimal_amount_1=, view_name=CU_0203_1207, decimal_amount_2=, decimal_amount_3=, premium_1=, view_type=S, short_code_2=, description=, premium_3=, short_code_1=, premium_2=, effective_date=2012-09-11, identifier=1kH, date_2=, date_1=}, {long_code_1=, address_number=, long_code_2=, view_desc=CU 21 11 (09 00) - Limitation of Coverage to Designated Premises or Project, name_and_address=, termination_date=null, reference_number=0, sequence_number=2, decimal_amount_1=, view_name=CU_2111_0900, decimal_amount_2=, decimal_amount_3=, premium_1=, view_type=D, short_code_2=, description=17000 Terraverde Cir., Fort Myers, FL 33908-4459>, premium_3=, short_code_1=1, premium_2=, effective_date=2012-09-11, identifier=1kL, date_2=, date_1=}, {long_code_1=, address_number=, long_code_2=, view_desc=CU 21 23 (02 02) - Nuclear Energy Liab Exc End, name_and_address=, termination_date=null, reference_number=0, sequence_number=1, decimal_amount_1=, view_name=CU_2123_0202, decimal_amount_2=, decimal_amount_3=, premium_1=, view_type=S, short_code_2=, description=, premium_3=, short_code_1=, premium_2=, effective_date=2012-09-11, identifier=1kN, date_2=, date_1=}, {long_code_1=, address_number=, long_code_2=, view_desc=CU 21 27 (12 04) Fungi Or Bacteria Exclusion, name_and_address=, termination_date=null, reference_number=0, sequence_number=1, decimal_amount_1=, view_name=CU_2127_1204, decimal_amount_2=, decimal_amount_3=, premium_1=, view_type=S, short_code_2=, description=, premium_3=, short_code_1=, premium_2=, effective_date=2012-09-11, identifier=1MV, date_2=, date_1=}, {long_code_1=, address_number=, long_code_2=, view_desc=CU 21 42 (12 04) Exclusion - Exterior Insulation & Finish System, name_and_address=, termination_date=null, reference_number=0, sequence_number=1, decimal_amount_1=, view_name=CU_2142_1204, decimal_amount_2=, decimal_amount_3=, premium_1=, view_type=S, short_code_2=, description=, premium_3=, short_code_1=, premium_2=, effective_date=2012-09-11, identifier=1MW, date_2=, date_1=}, {long_code_1=, address_number=, long_code_2=, view_desc=CU 24 32 (12 05) - Limited Coverage Territory, name_and_address=, termination_date=null, reference_number=0, sequence_number=1, decimal_amount_1=, view_name=CU_2432_1205, decimal_amount_2=, decimal_amount_3=, premium_1=, view_type=S, short_code_2=, description=, premium_3=, short_code_1=, premium_2=, effective_date=2012-09-11, identifier=1kp, date_2=, date_1=}, {long_code_1=, address_number=, long_code_2=, view_desc=CU 24 36 (12 05) Products-Completed Operations Aggregate Limit Of Insurance, name_and_address=, termination_date=null, reference_number=0, sequence_number=1, decimal_amount_1=, view_name=CU_2436_1205, decimal_amount_2=, decimal_amount_3=, premium_1=, view_type=D, short_code_2=, description=, premium_3=, short_code_1=, premium_2=, effective_date=2012-09-11, identifier=1Z3, date_2=, date_1=}, {long_code_1=, address_number=, long_code_2=, view_desc=IL 09 85 (01 08) - Disclosure Pursuant To Terrorism Risk Insurance Act, name_and_address=, termination_date=null, reference_number=0, sequence_number=8, decimal_amount_1=, view_name=IL0985_0108_U, decimal_amount_2=, decimal_amount_3=, premium_1=24, view_type=D, short_code_2=, description=, premium_3=, short_code_1=, premium_2=, effective_date=2012-09-11, identifier=1jY, date_2=, date_1=}, {long_code_1=, address_number=, long_code_2=, view_desc=IL 00 17 (11 98) Common Policy Conditions, name_and_address=, termination_date=null, reference_number=0, sequence_number=1, decimal_amount_1=, view_name=IL_0017_1198, decimal_amount_2=, decimal_amount_3=, premium_1=, view_type=S, short_code_2=, description=, premium_3=, short_code_1=, premium_2=, effective_date=2012-09-11, identifier=1NJ, date_2=, date_1=}, {long_code_1=, address_number=, long_code_2=, view_desc=IL 008 (11 08) - Total Lead Exclusion, name_and_address=, termination_date=null, reference_number=0, sequence_number=1, decimal_amount_1=, view_name=IL_008_1108, decimal_amount_2=, decimal_amount_3=, premium_1=, view_type=S, short_code_2=, description=, premium_3=, short_code_1=, premium_2=, effective_date=2012-09-11, identifier=1kw, date_2=, date_1=}, {long_code_1=, address_number=, long_code_2=, view_desc=IL 009 (11 08) - Total Asbestos Exclusion, name_and_address=, termination_date=null, reference_number=0, sequence_number=1, decimal_amount_1=, view_name=IL_009_1108, decimal_amount_2=, decimal_amount_3=, premium_1=, view_type=S, short_code_2=, description=, premium_3=, short_code_1=, premium_2=, effective_date=2012-09-11, identifier=1kx, date_2=, date_1=}, {long_code_1=, address_number=, long_code_2=, view_desc=IL 035 (07 07) Florida Assessments, name_and_address=, termination_date=null, reference_number=0, sequence_number=1, decimal_amount_1=, view_name=IL_035_0707_U, decimal_amount_2=, decimal_amount_3=, premium_1=, view_type=D, short_code_2=, description=, premium_3=, short_code_1=, premium_2=, effective_date=2012-09-11, identifier=1YE, date_2=, date_1=}, {long_code_1=, address_number=, long_code_2=, view_desc=UMB 176 (08 07) Florida Amendatory Endorsement, name_and_address=, termination_date=null, reference_number=0, sequence_number=1, decimal_amount_1=, view_name=UMB_176_0807, decimal_amount_2=, decimal_amount_3=, premium_1=, view_type=S, short_code_2=, description=, premium_3=, short_code_1=, premium_2=, effective_date=2012-09-11, identifier=1a4, date_2=, date_1=}, {long_code_1=, address_number=, long_code_2=, view_desc=UMB FL003 (07-97) Florida Customer Assistance Endorsement, name_and_address=, termination_date=null, reference_number=0, sequence_number=1, decimal_amount_1=, view_name=UMB_FL003_0797, decimal_amount_2=, decimal_amount_3=, premium_1=, view_type=S, short_code_2=, description=, premium_3=, short_code_1=, premium_2=, effective_date=2012-09-11, identifier=0cj, date_2=, date_1=}]
```

Have fun scrolling through that. I suppose you could paste it into a text editor and search through it, but doing that over and over again seems tedious to me (and not nearly as fun as using the shell). So I use the following command to pass in long strings of data and have it printed back to me with each pair on it's own line. By doing so, I can pipe the output into grep and get meaningful search results.
#### Print a long line of text delimited by commas onto individual lines:
{% include_code shell/pretty_print %}

Running that will produce something like this:

```
...
[_view_desc=CU_24_36_(12_05)_Products-Completed_Operations_Aggregate_Limit_Of_Insurance]
[_name_and_address=]
[_termination_date=null]
[_reference_number=0]
[_sequence_number=1]
[_decimal_amount_1=]
[_view_name=CU_2436_1205]
[_decimal_amount_2=]
[_decimal_amount_3=]
[_premium_1=]
[_view_type=D]
[_short_code_2=]
[_description=]
[_premium_3=]
[_short_code_1=]
[_premium_2=]
[_effective_date=2012-09-11]
[_identifier=1Z3]
[_date_2=]
[_date_1=}]
[_{long_code_1=]
[_address_number=]
[_long_code_2=]
...
```

Pipe into grep and search your term, and you get nice and neat search results:

```
[_identifier=1jR]
[_identifier=1kB]
[_identifier=1kH]
[_identifier=1kL]
[_identifier=1kN]
[_identifier=1MV]
[_identifier=1MW]
[_identifier=1kp]
[_identifier=1Z3]
[_identifier=1jY]
[_identifier=1NJ]
[_identifier=1kw]
[_identifier=1kx]
[_identifier=1YE]
[_identifier=1a4]
[_identifier=0cj]
```

Ah, much better.
